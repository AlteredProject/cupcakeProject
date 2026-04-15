document.getElementById("topping").addEventListener("change", calculatePrice);
document.getElementById("bottom").addEventListener("change", calculatePrice);
document.getElementById("amount").addEventListener("change", calculatePrice);

function calculatePrice(){
    const toppingSelect = document.getElementById("topping");
    const bottomSelect = document.getElementById("bottom");
    const amount = document.getElementById("amount").value;

    const toppingPrice = toppingSelect.options[toppingSelect.selectedIndex]?.dataset.price;
    const bottomPrice = toppingSelect.options[bottomSelect.selectedIndex]?.dataset.price;

    const total = (parseFloat(toppingPrice) + parseFloat(bottomPrice)) * parseInt(amount);
    if (total.valueOf()) {
        document.getElementById("totalPrice").innerText = "Samlet pris: " + total + " $";
    }
}

function basketBought(){
    const container = document.querySelector('.section-block-grid');
    container.innerHTML = '<h2>Tak for købet!' +
        '<h3>Du får en Email når du kan hente din ordrer</h3>';
}