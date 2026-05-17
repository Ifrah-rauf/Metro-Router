// Mock data for stations
const stations =
// Attach input elements and suggestion containers
const sourceInput = document.getElementById("source");
const destinationInput = document.getElementById("destination");
const sourceSuggestions = document.getElementById("source-suggestions");
const destinationSuggestions = document.getElementById("destination-suggestions");

// Function to filter suggestions
function filterSuggestions(input, suggestionsList, data) {
    const query = input.value.toLowerCase();
    suggestionsList.innerHTML = ""; // Clear previous suggestions

    if (query.length > 0) {
        const matches = data.filter(station => station.toLowerCase().includes(query));
        matches.forEach(match => {
            const suggestionItem = document.createElement("li");
            suggestionItem.textContent = match;

            // Add click event to select suggestion
            suggestionItem.addEventListener("click", () => {
                input.value = match;
                suggestionsList.innerHTML = ""; // Clear suggestions after selection
            });

            suggestionsList.appendChild(suggestionItem);
        });
    }
}

// Event listeners for input fields
sourceInput.addEventListener("input", () => {
    filterSuggestions(sourceInput, sourceSuggestions, stations);
});

destinationInput.addEventListener("input", () => {
    filterSuggestions(destinationInput, destinationSuggestions, stations);
});

// Click outside to close suggestion list
document.addEventListener("click", (event) => {
    if (!sourceSuggestions.contains(event.target) && event.target !== sourceInput) {
        sourceSuggestions.innerHTML = "";
    }
    if (!destinationSuggestions.contains(event.target) && event.target !== destinationInput) {
        destinationSuggestions.innerHTML = "";
    }
});
