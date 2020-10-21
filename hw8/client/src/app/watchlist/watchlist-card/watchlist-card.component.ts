import { Component, OnInit, Input, Output, EventEmitter } from '@angular/core';

@Component({
  selector: 'watchlist-card',
  templateUrl: './watchlist-card.component.html',
  styleUrls: ['./watchlist-card.component.css']
})
export class WatchlistCardComponent implements OnInit {
  @Input() watchedItem;
  @Output() delete = new EventEmitter();
  constructor() { }

  ngOnInit(): void {}

  onDelete() {
      this.delete.emit(this.watchedItem.ticker);
  }
}
