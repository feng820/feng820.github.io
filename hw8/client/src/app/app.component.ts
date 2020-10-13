import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { Location } from '@angular/common';

@Component({
  selector: 'stock-root',
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.css']
})
export class AppComponent{

  onActive() {
    const pathname = location.pathname;
    let index = 0;
    if (pathname === "/watchlist") {
      index = 1;
    } else if (pathname === "/portfolio") {
      index = 2;
    }
    const buttons = document.getElementById('tabs').getElementsByTagName('a');
    for (let i = 0; i < buttons.length; i++) {
      buttons[i].className = buttons[i].className.replace(" active", "");
    }

    buttons[index].className += ' active';
  }

}
