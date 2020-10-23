import { Component, Input, OnInit } from '@angular/core';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';

@Component({
  selector: 'app-news-modal',
  templateUrl: './news-modal.component.html',
  styleUrls: ['./news-modal.component.css']
})
export class NewsModalComponent implements OnInit {
  @Input() newsInfo;

  constructor(
    public activeModal: NgbActiveModal,
  ) { }

  ngOnInit(): void {
  }

}
