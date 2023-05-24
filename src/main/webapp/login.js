window.addEventListener("load", () => {
  const loginForm = document.querySelector("#login");
  loginForm.addEventListener("submit", async event => {
    console.log("Login form submitted");
    const res = await fetch("login", {
      method: "POST",
      body: new FormData(loginForm),
    });
    const data = await res.json();
    console.log("Parsed reposnse", data);

    if (data.success) {
      window.location.href = "index.html";
      sessionStorage.setItem("username", data.username);
    } else {
      const error = document.querySelector("#error-message");
      error.textContent = data.message;
    }
  });
});
