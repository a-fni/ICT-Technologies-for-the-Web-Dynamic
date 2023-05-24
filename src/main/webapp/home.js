window.addEventListener("load", () => {
  // restore username from session storage
  const userspan = document.querySelector("#username");
  const username = sessionStorage.getItem("username");
  userspan.textContent = username;
});
