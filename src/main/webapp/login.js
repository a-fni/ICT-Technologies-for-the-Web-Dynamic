window.addEventListener("load", () => {
  const loginForm = document.querySelector("#login");
  loginForm.addEventListener("submit", async event => {
    const res = await fetch("login", {
      method: "POST",
      body: new FormData(loginForm),
    });
    const data = await res.json();

    if (data.success) {
      window.location.href = "index.html";
      sessionStorage.setItem("username", data.username);
    } else {
      alert("Login failed");
    }
  });
});
