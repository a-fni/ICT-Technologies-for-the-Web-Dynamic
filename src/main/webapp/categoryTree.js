/**
 * Creates the category tree and appends it to the DOM
 * @param {Category[]} data all the data as an array of categories
 */
export function createCategoryTree(data) {
  const root = document.querySelector("#category-tree");
  while (root.firstChild) {
    root.removeChild(root.firstChild);
  }
  root.appendChild(createDivFromSubtree(data[0], data));
}

/**
 * Creates a div from a subtree
 * @param {Category} subtree
 * @param {Category[]} tree
 * @returns {HTMLDivElement} the div with the subtree
 */
export function createDivFromSubtree(subtree, tree) {
  // create the div
  const div = document.createElement("div");
  div.classList.add("category");

  // create the name span
  const span = document.createElement("span");
  span.classList.add("category-name");
  span.innerText = `${subtree.code} - ${subtree.name}`;
  const input = document.createElement("input");
  input.type = "text";
  input.name = "name";
  input.value = subtree.name;
  input.style.display = "none";
  span.appendChild(input);
  div.appendChild(span);
  div.appendChild(input);

  span.addEventListener("click", () => {
    span.style.display = "none";
    input.style.display = "block";
    input.focus();
  });
  input.addEventListener("blur", () => {
    span.style.display = "block";
    input.style.display = "none";
  });

  // get the children
  const children = tree.filter(
    s =>
      s.code.startsWith(subtree.code) &&
      s.code.length === subtree.code.length + 1
  );

  // create the children divs
  for (const child of children) {
    div.appendChild(createDivFromSubtree(child, tree));
  }

  return div;
}
