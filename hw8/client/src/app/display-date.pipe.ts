import { Pipe, PipeTransform } from '@angular/core';
import { DatePipe } from '@angular/common';

@Pipe({
    name: 'displayDate',
    pure: true
})
export class DisplayDatePipe extends DatePipe implements PipeTransform {
    transform(date): any {
        return super.transform(date, 'yyyy-MM-dd HH:mm:ss', 'UTC -8');
    }
}