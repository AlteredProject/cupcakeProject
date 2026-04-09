document.getElementById("topping").addEventListener("change", calculatePrice);
document.getElementById("bottom").addEventListener("change", calculatePrice);
document.getElementById("amount").addEventListener("change", calculatePrice);

function calculatePrice(){
    const topping = document.getElementById("topping").value;
    const bottom = document.getElementById("bottom").value;
    const amount = document.getElementById("amount").value;

    if (topping && bottom && amount){
        const total = (parseFloat(topping) + parseFloat(bottom)) * parseInt(amount);
        document.getElementById("totalPrice").innerText = "Samlet pris: " + total + " $";
    }
}