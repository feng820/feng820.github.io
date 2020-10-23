import { Component, Input, OnInit } from '@angular/core';
import { FormBuilder, FormGroup } from  '@angular/forms';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';


@Component({
  selector: 'stock-modal',
  templateUrl: './stock-modal.component.html',
  styleUrls: ['./stock-modal.component.css']
})
export class StockModalComponent implements OnInit {
  form: FormGroup;
  @Input() title: string;
  @Input() price: number;
  @Input() isBuy: boolean;
  @Input() quantity: number;
  constructor(
    public activeModal: NgbActiveModal,
    private formBuilder: FormBuilder,
  ) {}

  ngOnInit(): void {
    this.form = this.formBuilder.group({
      quantity: this.formBuilder.control(0)
    });
  }

  close() {
    this.activeModal.close(this.form.get('quantity').value);
  }

}
