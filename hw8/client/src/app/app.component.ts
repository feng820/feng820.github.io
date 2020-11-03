import { Component } from '@angular/core';
declare var $: any;

@Component({
  selector: 'stock-root',
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.css']
})
export class AppComponent{
  
  onActive() {
    const pathname = location.pathname;
    let index = -1;
    if (pathname === "/") {
      index = 0;
    } else if (pathname === "/watchlist") {
      index = 1;
    } else if (pathname === '/portfolio') {
      index = 2
    }
    const buttons = document.getElementById('tabs').getElementsByTagName('a');
    for (let i = 0; i < buttons.length; i++) {
      buttons[i].className = buttons[i].className.replace(" active", "");
    }

    if (index !== -1) {
      buttons[index].className += ' active';
      $(".navbar-collapse").collapse('hide')
    }

    $(".navbar-toggler").removeClass("toggler-border");
  }

  onExpand() {
    $(".navbar-toggler").addClass("toggler-border");
  }

}
