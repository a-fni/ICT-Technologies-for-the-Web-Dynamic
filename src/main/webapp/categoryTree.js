// Global variable in charge of signalling when a copy is in progress
let isCloningCategory = false;

// Global object in charge of storing copying information
let copyInfo = {
  src: null,
  dest: null,
}


/**
 * Creates the category tree and appends it to the DOM
 *
 * @param {Category[]} data all the data as an array of categories
 */
function createCategoryTree(data) {
  const root = document.querySelector("#category-tree");

  // Removing any previous category tree first
  while (root.firstChild) {
    root.removeChild(root.firstChild);
  }
  root.appendChild(createDivFromSubtree(data[0], data));
}

/**
 * Creates a div from a subtree
 *
 * @param {Category} subtree
 * @param {Category[]} tree
 * @returns {HTMLDivElement} the div with the subtree
 */
export function createDivFromSubtree(subtree, tree) {
  // Create the node's div
  const div = document.createElement("div");
  div.classList.add("category");

  div.id = subtree.code;

  div.draggable = "true";
  div.addEventListener(
    "dragstart",
    event => {
      if (isCloningCategory) {
        event.preventDefault();
      } else {
        event.stopPropagation();
        console.log(`started dragging ${subtree.code}`);
        event.dataTransfer.setData("text/plain", subtree.code);
      }
    },
    {
      capture: false,
    }
  );
  div.addEventListener("dragover", event => {
    event.preventDefault();
    event.stopPropagation();
    div.classList.add("dragover");
  });
  div.addEventListener("dragleave", event => {
    event.preventDefault();
    event.stopPropagation();
    div.classList.remove("dragover");
  });
  div.addEventListener(
    "drop",
    event => {
      event.preventDefault();
      event.stopPropagation();

      if (isCloningCategory) return;
      if (!subtree.parentable) {
        alert(`Category ${subtree.code} cannot have any more children`);
        return;
      }

      // if parentable clone the tree
      // get the code
      const draggedCode = event.dataTransfer.getData("text/plain");
      console.log(`dropped ${draggedCode} on ${subtree.code}`);

      const confirmed = confirm(
        `Do you want to clone the category ${draggedCode} into ${subtree.name} (${subtree.code})?`
      );

      if (!confirmed) {
        return;
      }

      const draggedDiv = document.getElementById(draggedCode);
      if (draggedDiv) {
        // clone the node
        const clone = draggedDiv.cloneNode(true);
        // change the code span text
        clone.querySelectorAll(".category-name").forEach(e => {
          e.innerText = "* - ";
        });
        clone.classList.add("cloned");
        event.currentTarget.appendChild(clone);

        // Showing cancel-save buttons
        document.querySelector(".copy-buttons").style.display = "flex";
        document.querySelector("#create-category-button").disabled = true;

        // Setting copying variables
        copyInfo.src = draggedCode || "/";
        copyInfo.dest = subtree.code || "/";
        isCloningCategory = true;
      }
    },
    {
      capture: false,
    }
  );

  // Create the code and name spans
  const codeSpan = document.createElement("span");
  codeSpan.classList.add("category-name");
  codeSpan.innerText = `${subtree.code} - `;

  const nameSpan = document.createElement("span");
  nameSpan.classList.add("current-name");
  nameSpan.innerText = `${subtree.name}`;

  // Creating the rename form
  const renameForm = document.createElement("form");
  renameForm.classList.add("rename-form");
  renameForm.addEventListener("submit", event => {
    event.preventDefault();
    void renameCategory(div);
  });

  // Node code should be a hidden input of the form
  const code = document.createElement("input");
  code.classList.add("code");
  code.type = "text";
  code.name = "code";
  code.value = subtree.code;
  code.type = "hidden";

  // Create the (initially hidden) new-name input of the form
  const newName = document.createElement("input");
  newName.classList.add("new-name");
  newName.type = "text";
  newName.name = "newName";
  newName.value = subtree.name;
  newName.style.display = "none";

  // Constructing DOM
  renameForm.appendChild(code);
  renameForm.appendChild(newName);
  div.appendChild(codeSpan);
  div.appendChild(nameSpan);
  div.appendChild(renameForm);

  // Adding on-click editing functionality
  nameSpan.addEventListener("click", () => {
    if (isCloningCategory) {
      alert("Cannot rename category while cloning another one");
      return;
    }

    // Root can never be renamed
    if (!subtree.code) {
      alert("Cannot rename the root of the tree!");
      return;
    }

    nameSpan.style.display = "none";
    newName.style.display = "inline";
    newName.focus();
  });
  newName.addEventListener("blur", () => {
    void renameCategory(div);
  });

  // Get the children...
  const children = tree.filter(
    s =>
      s.code.startsWith(subtree.code) &&
      s.code.length === subtree.code.length + 1
  );

  // ...and recursively add them to the current node's div
  for (const child of children) {
    div.appendChild(createDivFromSubtree(child, tree));
  }

  return div;
}

/**
 * Sends async request to rename a category, then redraws full category tree
 * @param {HTMLDivElement} nodeDiv div associated with the category we want to rename
 * @returns {Promise<void>} async call Promise
 */
async function renameCategory(nodeDiv) {
  // Fetching the the form, the spans and the new-name input
  const renameForm = nodeDiv.querySelector(".rename-form");
  const currentName = nodeDiv.querySelector(".current-name");
  const newName = nodeDiv.querySelector(".new-name");

  // Disabling editing
  currentName.style.display = "inline";
  newName.style.display = "none";

  // Cleaning input
  newName.value = newName.value.trim();

  // As long as the input hasn't been left empty and has changed, we send a rename request
  if (newName.value !== "" && newName.value !== currentName.innerText.trim()) {
    // At first, we update the span's content. Its correct value
    // will be set upon full category-tree redraw
    currentName.innerText = newName.value;

    const res = await fetch("renameCategory", {
      method: "POST",
      headers: {
        "Content-Type": "application/x-www-form-urlencoded",
      },
      body: new URLSearchParams(new FormData(renameForm)),
    });

    // Handling response
    const data = await res.json();
    if (!data.success) alert(data.message);

    // Finally, reloading the entire category-tree
    void fetchCategories();
  } else {
    // If the input was empty, reset it's value...
    newName.value = currentName.innerText.trim();
  }
}


/**
 * Fetches asynchronously the ful category-tree and renders it
 * @returns {Promise<void>} async call Promise
 */
export async function fetchCategories() {
  // fetch the categories on load
  const res = await fetch("categories");

  // Handling response
  if (!res.ok) {
    alert("Something went wrong while fetching categories... retry later");
    return;
  }

  /** @type {Category[]} */
  const data = await res.json();
  // console.log("Parsed response", data);  // DEBUG ONLY
  createCategoryTree(data);

  // Populating create form parent-select
  const parentables = data.filter(c => c.parentable);
  const parentSelect = document.querySelector("#create-select");
  parentSelect.innerHTML = parentables
      .map(c => `<option value="${c.code || "/"}">${c.name}</option> `)
      .join("");
}

window.addEventListener("load", () => {
  // Cancel copy handling
  const cancelButton = document.querySelector("#cancel-button");
  cancelButton.addEventListener("click", () => {
    // Hiding cancel-save buttons
    document.querySelector(".copy-buttons").style.display = "none";
    document.querySelector("#create-category-button").disabled = false;
    // Resetting copying variables
    copyInfo.src = copyInfo.dest = null;
    isCloningCategory = false;

    // Fetching updated app state
    void fetchCategories();
  });

  // Save copy handling
  const saveButton = document.querySelector("#save-button");
  saveButton.addEventListener("click", async () => {
    if (copyInfo.src && copyInfo.dest) {
      const res = await fetch("doCopy", {
        method: "POST",
        headers: {
          "Content-Type": "application/x-www-form-urlencoded",
        },
        body: `src=${copyInfo.src}&dest=${copyInfo.dest}`
      });

      const data = await res.json();
      if (!data.success) alert(data.message);

      // Hiding cancel-save buttons
      document.querySelector(".copy-buttons").style.display = "none";
      document.querySelector("#create-category-button").disabled = false;

      // Resetting copying variables
      copyInfo.src = copyInfo.dest = null;
      isCloningCategory = false;

      // Fetching updated app state
      void fetchCategories();
    }
  });
});
