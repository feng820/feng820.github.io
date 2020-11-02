import { Pipe, PipeTransform } from '@angular/core';

@Pipe({
    name: 'displayNumber',
    pure: true
})
export class DisplayNumberPipe implements PipeTransform {
    transform(number, removeDecimal?) {
        if (typeof number === 'number') {
            number = number.toFixed(2);
            let formattedNum = number.toString().split(".");
            formattedNum[0] = formattedNum[0].replace(/\B(?=(\d{3})+(?!\d))/g, ",");
            let ret = formattedNum.join(".");
            if (removeDecimal) {
                return ret.split('.')[0];
            } else {
                return ret;
            }
        }
        return number;
    }
}