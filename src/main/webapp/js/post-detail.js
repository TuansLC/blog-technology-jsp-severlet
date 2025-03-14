document.addEventListener("DOMContentLoaded", () => {
  // Rating functionality
  const ratingStars = document.querySelectorAll("#rating .bi-star")
  let currentRating = 0
  let totalRatings = 0
  let averageRating = 0

  ratingStars.forEach((star) => {
    star.addEventListener("click", function () {
      const rating = this.getAttribute("data-rating")
      setRating(rating)
      updateAverageRating(rating)
    })

    star.addEventListener("mouseover", function () {
      const rating = this.getAttribute("data-rating")
      highlightStars(rating)
    })

    star.addEventListener("mouseout", () => {
      highlightStars(currentRating)
    })
  })

  function setRating(rating) {
    currentRating = rating
    highlightStars(rating)
  }

  function highlightStars(rating) {
    ratingStars.forEach((star) => {
      const starRating = star.getAttribute("data-rating")
      if (starRating <= rating) {
        star.classList.remove("bi-star")
        star.classList.add("bi-star-fill")
      } else {
        star.classList.remove("bi-star-fill")
        star.classList.add("bi-star")
      }
    })
  }

  function updateAverageRating(newRating) {
    totalRatings++
    averageRating = (averageRating * (totalRatings - 1) + Number.parseInt(newRating)) / totalRatings
    document.getElementById("averageRating").textContent = averageRating.toFixed(1)
    document.getElementById("totalRatings").textContent = totalRatings
  }

  // Comment functionality
  const commentForm = document.getElementById("commentForm")
  const commentsContainer = document.getElementById("comments")

  commentForm.addEventListener("submit", (e) => {
    e.preventDefault()
    const name = document.getElementById("commentName").value
    const email = document.getElementById("commentEmail").value
    const content = document.getElementById("commentContent").value

    addComment(name, email, content)
    commentForm.reset()
  })

  function addComment(name, email, content) {
    const commentElement = document.createElement("div")
    commentElement.classList.add("card", "mb-3")
    commentElement.innerHTML = `
            <div class="card-body">
                <h5 class="card-title">${name}</h5>
                <h6 class="card-subtitle mb-2 text-muted">${email}</h6>
                <p class="card-text">${content}</p>
            </div>
        `
    commentsContainer.appendChild(commentElement)
  }
})

