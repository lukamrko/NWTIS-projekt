var wsUri="ws://localhost:8380/lmrkonjic_zadaca_3_3/ws";
console.log("Usao u websocket.js");
const webSocket=new WebSocket(wsUri);
webSocket.onMessage=onMessage;

function onMessage(evt)
{
    console.log("Pristigla poruka");
    const buton=document.getElementById('obrazac:osvjezi');
    buton.click();
}