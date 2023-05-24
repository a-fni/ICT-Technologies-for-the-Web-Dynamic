import { createCategoryTree } from "./categoryTree.js";

window.addEventListener("load", () => {
  // restore username from session storage
  const userspan = document.querySelector("#username");
  const username = localStorage.getItem("username");
  userspan.textContent = username;

  const createForm = document.querySelector("#createCategory");
  createForm.addEventListener("submit", async event => {
    event.preventDefault();
    console.log("Create form submitted");
    const res = await fetch("createCategory", {
      method: "POST",
      headers: {
        "Content-Type": "application/x-www-form-urlencoded",
      },
      body: new URLSearchParams(new FormData(createForm)),
    });
    const data = await res.json();
    fetchCategories();
    console.log("Parsed response", data);
  });
});

async function fetchCategories() {
  // fetch the categories on load
  const res = await fetch("categories");

  if (!res.ok) {
    console.log("Error fetching categories", data.message);
    return;
  }

  /** @type {Category[]} */
  const data = await res.json();
  console.log("Parsed response", data);
  createCategoryTree(data);
  const parentables = data.filter(c => c.parentable);
  const parentSelect = document.querySelector("#create-select");
  parentSelect.innerHTML = parentables
    .map(c => `<option value="${c.code || "/"}">${c.name}</option> `)
    .join("");
}

window.addEventListener("load", () => {
  fetchCategories();
});
