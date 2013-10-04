var speed = 150;
var endChar = " ";
var pos = 0;
 
function moveTitle(msg)
{
  var ml = msg.length;
       
  title = msg.substr(pos,ml) + endChar + msg.substr(0,pos);
  document.title = title;
   
  pos++;
  if (pos > ml) {
	  pos=0;
  }
  window.setTimeout(function() {
	  moveTitle(msg);
  },speed);
}
$(document).ready(function() {
  moveTitle(document.title);
});