window.addEventListener("load", () => {
  // Login event handling
  const loginForm = document.querySelector("#login");
  loginForm.addEventListener("submit", async event => {
    event.preventDefault();

    const res = await fetch("login", {
      method: "POST",
      headers: {
        "Content-Type": "application/x-www-form-urlencoded",
      },
      body: new URLSearchParams(new FormData(loginForm)),
    });

    // Checking response status is 200
    if (!res.ok) {
      alert(`An error ${res.status} was returned from the server.`);
      return;
    }

    // Handling response
    const data = await res.json();
    // console.log("Parsed response", data);  // DEBUG ONLY

    if (data.success) {
      localStorage.setItem("username", data.username);
      window.location.href = "home.html";
    } else {
      const error = document.querySelector("#error-message");
      error.textContent = data.message;
    }
  });
});
