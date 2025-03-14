document.addEventListener("DOMContentLoaded", () => {
  // Back to Top button functionality
  var backToTopButton = document.getElementById("backToTop")

  window.onscroll = () => {
    if (document.body.scrollTop > 20 || document.documentElement.scrollTop > 20) {
      backToTopButton.style.display = "block"
    } else {
      backToTopButton.style.display = "none"
    }
  }

  backToTopButton.addEventListener("click", () => {
    document.body.scrollTop = 0 // For Safari
    document.documentElement.scrollTop = 0 // For Chrome, Firefox, IE and Opera
  })

  // Newsletter form submission
  var newsletterForm = document.getElementById("newsletterForm")
  if (newsletterForm) {
    newsletterForm.addEventListener("submit", (e) => {
      e.preventDefault()
      var email = document.getElementById("newsletterEmail").value
      alert("Thank you for subscribing with email: " + email)
      document.getElementById("newsletterModal").classList.remove("show")
      document.body.classList.remove("modal-open")
      document.getElementsByClassName("modal-backdrop")[0].remove()
    })
  }

  // Dark mode toggle
  var darkModeToggle = document.getElementById("darkModeToggle")
  var icon = darkModeToggle.querySelector("i")

  darkModeToggle.addEventListener("click", () => {
    document.body.classList.toggle("dark-mode")
    if (document.body.classList.contains("dark-mode")) {
      icon.classList.remove("bi-moon")
      icon.classList.add("bi-sun")
    } else {
      icon.classList.remove("bi-sun")
      icon.classList.add("bi-moon")
    }
  })
})

