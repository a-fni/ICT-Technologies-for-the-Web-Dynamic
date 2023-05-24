import {createCategoryTree} from "./categoryTree.js";


window.addEventListener("load", () => {
  // Display username from session storage
  const userspan = document.querySelector("#username");
  userspan.textContent = localStorage.getItem("username");

  // Category creation handling
  const createForm = document.querySelector("#createCategory");
  createForm.addEventListener("submit", async event => {
    event.preventDefault();

    const res = await fetch("createCategory", {
      method: "POST",
      headers: {
        "Content-Type": "application/x-www-form-urlencoded",
      },
      body: new URLSearchParams(new FormData(createForm)),
    });

    // Handling response
    const data = await res.json();
    if (!data.success) alert(data.message);
    createForm.reset();

    // We always reload the entire tree at the end
    void fetchCategories();
  });
});


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
  // Rendering full category-tree
  void fetchCategories();
});
