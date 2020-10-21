import { Component, OnInit } from '@angular/core';

@Component({
    selector: 'watchlist',
    templateUrl: './watchlist.component.html',
    styleUrls: ['./watchlist.component.css']
})

export class WatchlistComponent implements OnInit{
    watchList: Array<any>

    constructor() {
        this.watchList = JSON.parse(localStorage.getItem("watchlist") || "[]" );
    }

    ngOnInit() {}

    onCardDelete(ticker) {
        for (let i = 0; i < this.watchList.length; i++) {
            if (this.watchList[i].ticker == ticker) {
                this.watchList.splice(i, 1);
            }
        }
        localStorage.setItem('watchlist', JSON.stringify(this.watchList));
    }


}