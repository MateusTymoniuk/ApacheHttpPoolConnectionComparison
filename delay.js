// delay.js
module.exports = (req, res, next) => {
    setTimeout(next, getRandomArbitrary())
}

// Generate a random arbitrary number between 0 and the max value
function getRandomArbitrary() {
    max = 0;
    return Math.random() * max;
}

