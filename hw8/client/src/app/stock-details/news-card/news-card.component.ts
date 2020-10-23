import { Component, Input, OnInit } from '@angular/core';
import { NewsModalComponent } from '../news-modal/news-modal.component';
import { NgbModal } from '@ng-bootstrap/ng-bootstrap';
import { THIS_EXPR } from '@angular/compiler/src/output/output_ast';

@Component({
  selector: 'news-card',
  templateUrl: './news-card.component.html',
  styleUrls: ['./news-card.component.css']
})
export class NewsCardComponent implements OnInit {
  @Input() newsInfo
  constructor(
    private modalService: NgbModal,
  ) { }

  ngOnInit(): void {
  }

  open() {
    const modalRef = this.modalService.open(NewsModalComponent);
    modalRef.componentInstance.newsInfo = this.newsInfo;
  }

}
