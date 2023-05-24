window.addEventListener("load", () => {
  const loginForm = document.querySelector("#login");
  loginForm.addEventListener("submit", async event => {
    event.preventDefault();
    console.log("Login form submitted");
    const res = await fetch("login", {
      method: "POST",
      body: new FormData(loginForm),
    });
    const data = await res.json();
    console.log("Parsed response", data);

    if (data.success) {
      sessionStorage.setItem("username", data.username);
      window.location.href = "index.html";
    } else {
      const error = document.querySelector("#error-message");
      error.textContent = data.message;
    }
  });
});
