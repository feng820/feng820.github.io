import { Component, Input, OnInit } from '@angular/core';

@Component({
  selector: 'news-card',
  templateUrl: './news-card.component.html',
  styleUrls: ['./news-card.component.css']
})
export class NewsCardComponent implements OnInit {
  @Input() newsInfo
  constructor() { }

  ngOnInit(): void {
  }

}
