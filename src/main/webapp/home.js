window.addEventListener("load", () => {
  // restore username from session storage
  const userspan = document.querySelector("#username");
  const username = localStorage.getItem("username");
  userspan.textContent = username;
});

window.addEventListener("load", async () => {
  // fetch the categories on load
  const res = await fetch("categories");
  const data = await res.json();
  console.log("Parsed response", data);

  if (!data.success) {
    console.log("Error fetching categories", data.message);
    return;
  }

  const table = document.querySelector("#category-tree");
  for (const category of data.data) {
    const tr = document.createElement("tr");
    const td = document.createElement("td");
    td.textContent = category.name;
    tr.appendChild(td);
    table.appendChild(tr);
  }
});
