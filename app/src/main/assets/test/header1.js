const burger = document.querySelector('.burger');
const drawer = document.querySelector('.drawer');
const shadow = document.getElementById("shadow");
drawer.style.right = '-250px';
       
function togglemenu(){
    if (drawer.style.right === '-250px') {
        openDrawer();
      } else {
        closeDrawer();
      }
}

function openDrawer(){
  document.body.style.overflow = 'hidden';
  burger.classList.toggle('toggle');
  if (drawer.style.right === '-250px') {
    drawer.style.right = '0';
    shadow.style.display = 'block';
  }
}

function closeDrawer(){
  document.body.style.overflow = 'auto';
  burger.classList.toggle('toggle');
  if (drawer.style.right === '0px') {
    console.log('close')
    drawer.style.right = '-250px';
    shadow.style.display = 'none';
  }
}
