import { Component, EventEmitter, Input, OnInit, Output } from '@angular/core';
import { NgbModal } from '@ng-bootstrap/ng-bootstrap';
import { StockModalComponent } from '../../stock-modal/stock-modal.component';


@Component({
  selector: 'portfolio-card',
  templateUrl: './portfolio-card.component.html',
  styleUrls: ['./portfolio-card.component.css']
})
export class PortfolioCardComponent implements OnInit {
  @Input() portfolioItem;
  @Input() portfolio: Array<any>;
  @Output() updatePrice = new EventEmitter();
  constructor(
    private modalService: NgbModal,
  ) { }

  ngOnInit(): void {
  }

  private round(num) {
    return Math.round((num + Number.EPSILON) * 1000) / 1000;
  }

  findIndexInPortfolio(ticker) {
    let tickerInfo;
    for (let i = 0; i < this.portfolio.length; i++) {
        tickerInfo = this.portfolio[i];
        if (tickerInfo.ticker == ticker) {
            return i;
        }
    }
    return -1;
  }

  removeFromPortfolio(index) {
    this.portfolio.splice(index, 1);
    localStorage.setItem('portfolio', JSON.stringify(this.portfolio));
  }

  buyShares(quantity) {
    const index = this.findIndexInPortfolio(this.portfolioItem.ticker);
    if (index !== -1) {
      const portfolioInfo = this.portfolio[index];
      portfolioInfo.quantity += quantity;
      portfolioInfo.totalCost += quantity * this.portfolioItem.currentPrice;
      portfolioInfo.totalCost = this.round(portfolioInfo.totalCost);
      portfolioInfo.avgCostPerShare = this.round(portfolioInfo.totalCost / portfolioInfo.quantity);

      portfolioInfo.currentPrice = this.portfolioItem.currentPrice;
      portfolioInfo.change = this.round(portfolioInfo.currentPrice - portfolioInfo.avgCostPerShare);
      portfolioInfo.marketValue = this.round(portfolioInfo.currentPrice * portfolioInfo.quantity);
    } 

    localStorage.setItem('portfolio', JSON.stringify(this.portfolio));
    this.updatePrice.emit();
  }

  sellShares(quantity) {
    const index = this.findIndexInPortfolio(this.portfolioItem.ticker);
    if (index !== -1) {
      const portfolioInfo = this.portfolio[index];
      portfolioInfo.quantity -= quantity;
      portfolioInfo.totalCost -= quantity * this.portfolioItem.currentPrice;
      portfolioInfo.totalCost = this.round(portfolioInfo.totalCost);
      portfolioInfo.avgCostPerShare = this.round(portfolioInfo.totalCost / portfolioInfo.quantity);

      portfolioInfo.currentPrice = this.portfolioItem.currentPrice;
      portfolioInfo.change = this.round(portfolioInfo.currentPrice - portfolioInfo.avgCostPerShare);
      portfolioInfo.marketValue = this.round(portfolioInfo.currentPrice * portfolioInfo.quantity);

      if (portfolioInfo.quantity === 0) {
        this.removeFromPortfolio(index);
      }
    }

    localStorage.setItem('portfolio', JSON.stringify(this.portfolio));
    this.updatePrice.emit();
  }

  buy() {
    const modalRef = this.modalService.open(StockModalComponent);
    modalRef.componentInstance.title = this.portfolioItem.ticker;
    modalRef.componentInstance.price = this.portfolioItem.currentPrice;
    modalRef.componentInstance.isBuy = true;
    modalRef.result.then(quantity => {
        if (Number.isInteger(quantity)) {
            this.buyShares(quantity);
        }
    }, reject => {})
  }

  sell() {
    const modalRef = this.modalService.open(StockModalComponent);
    modalRef.componentInstance.title = this.portfolioItem.ticker;
    modalRef.componentInstance.price = this.portfolioItem.currentPrice;
    modalRef.componentInstance.isBuy = false;
    modalRef.componentInstance.quantity = this.portfolioItem.quantity;
    modalRef.result.then(quantity => {
        if (Number.isInteger(quantity)) {
          this.sellShares(quantity);
        }
    }, reject => {})
  }
  

}
