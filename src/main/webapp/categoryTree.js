import {fetchCategories} from "./home.js";


/**
 * Creates the category tree and appends it to the DOM
 *
 * @param {Category[]} data all the data as an array of categories
 */
export function createCategoryTree(data) {
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
  codeSpan.appendChild(nameSpan);
  renameForm.appendChild(code);
  renameForm.appendChild(newName);
  div.appendChild(codeSpan)
  div.appendChild(renameForm);

  // Adding on-click editing functionality
  codeSpan.addEventListener("click", () => {
    codeSpan.style.display = "none";
    newName.style.display = "block";
    newName.focus();
  });
  newName.addEventListener("blur", () => { void renameCategory(div); });

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
 * @param nodeDiv div associated with the category we want to rename
 * @returns {Promise<void>} async call Promise
 */
async function renameCategory(nodeDiv) {
  // Fetching the the form, the spans and the new-name input
  const renameForm = nodeDiv.querySelector(".rename-form");
  const codeSpan = nodeDiv.querySelector(".category-name");
  const currentName = nodeDiv.querySelector(".current-name");
  const newName = nodeDiv.querySelector(".new-name");

  // Disabling editing
  codeSpan.style.display = "block";
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
      body: new URLSearchParams(new FormData(renameForm))
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
