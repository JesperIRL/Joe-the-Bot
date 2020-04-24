var Discord = require('discord.js');
var auth = require('./auth.json');
var bot = new Discord.Client();
var active = false;
var games = [];

console.log('Did something!');
bot.login(auth.token);

bot.on('ready', () => {
   console.log('Ready!\n');
});

function findGame(game) {
   for (let i = 0; i < games.length; i++) {
      if (games[i].name === game) {
         return games[i];
      }
   }
   return null;
}

function removeGame(game) {
   for (let i = 0; i < games.length; i++) {
      if (games[i].name === game) {
         let game = games[i];
         games.splice(i, 1);
         return game;
      }
   }
   return null;
}

function intToWord(n) {
   switch (n) {
      case 0: return "rock";
      case 1: return "paper";
      case 2: return "scissors";
   }
   return "error";
}

bot.on('message', message => {
   if (message.content === "!k9 activate") {
      active = true;
      message.channel.send("Master?");
   }

   if (active) {
      if (message.content === "!k9 sleep") {
         message.channel.send('Affirmative.');
         active = false;
      } else if (message.content === "!k9 game") {
         if (findGame("RPS")) {
            message.channel.send('Game already in progres.');
            return;
         }
         my_choice = Math.floor(Math.random() * 3);
         games.push({name:"RPS", current:my_choice});
         message.channel.send("New game started. Choose one; rock, paper, or scissors");
      } else if ((message.content === "!rock") || (message.content === "!paper") || (message.content === "!scissors")) {
         let game = removeGame("RPS");
         if (!game) {
            message.channel.send("No game in progress.");
            return;
         }

         let guess = 0;
         if (message.content === "!paper") {
            guess = 1;
         } else if (message.content === "!scissors") {
            guess = 2;
         }

         if (game.current == guess) {
            message.channel.send("Same as me - Draw!");
         } else if (game.current == (guess + 1) % 3) {
            message.channel.send("I had " + intToWord(game.current) + " - I win!!");
         } else {
            message.channel.send("I had " + intToWord(game.current) + " - You win!");
         }
      }
   }
});
