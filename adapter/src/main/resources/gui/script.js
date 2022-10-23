async function refresh() {
    await fetch("api/images")
        .then(response => response.json())
        .then(data => {
            const rating = data.rating;
            if (rating != null) {
                updateRating(rating);
            }
            const requested = data.requested;
            if (requested != null) {
                updateRequested(requested);
            }
        })
}

async function newEvaluation() {
    let difficulty = document.getElementById("difficulty").selectedIndex;
    difficulty = difficulty === 0 ? "easy" : difficulty === 1 ? "medium" : "difficult";
    await requestImage(difficulty);
}

async function requestImage(difficulty) {
    await fetch(`api/request-new/${difficulty}`, {
        method: "GET"
    }).then(result => result.json())
        .then(data => {
            console.log(data);
            if (!data) {
                document.getElementById("request-result").innerHTML = `<p>Something went wrong</p>`
                return
            }
            const {id, score, maxScore, image} = data
            document.getElementById("request-result").innerHTML = `<p>Id: ${id}</p><p>Score: ${score}</p><p>Max score: ${maxScore}</p><img src="api/${image}" alt="${image}" />`
        })
}

function updateRating(images) {
    document.getElementById("rating-images").innerHTML = createImageList(images);
}

function updateRequested(images) {
    document.getElementById("requested-images").innerHTML = createImageList(images);
}

function createImageList(images) {
    let list = ""
    for (const image of images) {
        list += `<li><h3>${image.replace("images", "").replace("rating", "").replace("requested", "").replace("/", "").replace("/", "").trim()}</h3><img src="api/${image}" alt="${image}" /> </li>`;
    }
    return list;
}

