document.getElementById("topping").addEventListener("change", calculatePrice);
document.getElementById("bottom").addEventListener("change", calculatePrice);
document.getElementById("amount").addEventListener("change", calculatePrice);

function calculatePrice(){
    const toppingSelect = document.getElementById("topping");
    const bottomSelect = document.getElementById("bottom");
    const amount = document.getElementById("amount").value;

    const toppingPrice = toppingSelect.options[toppingSelect.selectedIndex]?.dataset.price;
    const bottomPrice = toppingSelect.options[bottomSelect.selectedIndex]?.dataset.price;

    if (toppingSelect && bottomSelect && amount > 0){
        const total = (parseFloat(toppingPrice) + parseFloat(bottomPrice)) * parseInt(amount);
        document.getElementById("totalPrice").innerText = "Samlet pris: " + total + " $";
    }
}