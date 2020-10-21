import { Component, EventEmitter, OnInit, Output } from '@angular/core';
import { FormBuilder, FormGroup } from  '@angular/forms';
import {NgbActiveModal} from '@ng-bootstrap/ng-bootstrap';


@Component({
  selector: 'stock-modal',
  templateUrl: './stock-modal.component.html',
  styleUrls: ['./stock-modal.component.css']
})
export class StockModalComponent implements OnInit {
  form: FormGroup;
  @Output() close = new EventEmitter();
  constructor(
    private activeModal: NgbActiveModal,
    private formBuilder: FormBuilder,
  ) {}

  ngOnInit(): void {
    this.form = this.formBuilder.group({
      quantity: this.formBuilder.control(0)
    });
  }

  buy() {
    this.activeModal.close('buy');
  }
}
