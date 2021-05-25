// consts
const prediction = document.getElementsByClassName('prediction')[0]
const Probability = document.getElementsByClassName('Probability')[0]
const radios = document.querySelectorAll('input[name="gender"]')
const saved = document.getElementsByClassName('saved')[0]
const clear = document.getElementsByClassName('clear')[0]
let name;

const form = document.forms['register-form'];

// form listener for submit
form.addEventListener('submit', async function (e){
    e.preventDefault();

    name = form.querySelector('input[name="name"]').value;

    // api
    const request = await fetch(`https://api.genderize.io/?name=${name}`)
    const result =  await request.json()

    saved_result = getFromLocal(name)

    if (saved_result){
        saved.innerHTML = saved_result
    }

    // for illegal input
    if (result.gender == null) {
        const x = document.getElementById("toast")
        x.className = "show";


        //hide toast after 4 seconds
        setTimeout(function(){
            x.className = x.className.replace("show", "");}, 4000);

    }else {
        prediction.innerHTML = result.gender
        Probability.innerHTML = result.probability

        // save radio button in local storage
        for (const radio of radios){
            if (radio.checked){
                storeInLocal(name, radio.value)
                break
            }
        }

    }

})

// Listener for clear
clear.addEventListener("click", () => clearFromLocal(name))


// local storage
const storeInLocal = function (key, value) {
    localStorage.setItem(key, value)

    saved.innerHTML = value
}

// Clear local storage
const clearFromLocal = function (key) {
    localStorage.removeItem(key);
    saved.innerHTML = "&nbsp"
}

// Get value by key from local storage
const getFromLocal = function (key){
    return localStorage.getItem(key)
}