import {fetchCategories} from "./categoryTree.js";


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

  // Logout handling
  const logoutButton = document.querySelector("#logout-button");
  logoutButton.addEventListener("click", () => {
    localStorage.removeItem("username");
    window.location.href = "logout";
  });
});


window.addEventListener("load", () => {
  // Rendering full category-tree
  void fetchCategories();
});
