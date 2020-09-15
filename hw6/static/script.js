addEventListenerForButton = (id) => {
    document.getElementById(id).addEventListener("click", (e) => {
        const button = document.getElementById(id);
        button.className += ' active';
        document.addEventListener('mousedown', () => {
            if (button.classList.contains('active')) {
                button.classList.remove('active');
            }
        }, {once: true})
    });
}
addEventListenerForButton('search-btn');
addEventListenerForButton('clear-btn');

let tickerName = null;

changeTab = (tabName, index) => {
    const allTabLinks = document.getElementsByClassName('tab-links');
    const allTabContent = document.getElementsByClassName('tab-content')
    for (let i = 0; i < allTabContent.length; i++) {
        allTabLinks[i].className = allTabLinks[i].className.replace(" active", "");
    }
    for (let i = 0; i < allTabContent.length; i++) {
        allTabContent[i].style.display = "none";
    }

    document.getElementById(tabName).style.display = "block";
    allTabLinks[index].className += ' active';
}

initDefaultView = (data) => {
    tickerName = data['ticker'];

    document.getElementById('tab-options').style.display = 'block';
    document.getElementById('outlook').style.display = 'block';

    document.getElementsByClassName('tab-links')[0].className += ' active';
    const dataList = document.getElementById('outlook').getElementsByClassName('data');
    dataList[0].innerHTML = data['name'];
    dataList[1].innerHTML = tickerName;
    dataList[2].innerHTML = data['exchangeCode'];
    dataList[3].innerHTML = data['startDate'];
    document.getElementById('outlook-description').innerHTML = data['description'];
}

initSummaryView = (ticker) => {
    fetch('/summary/' + ticker)
    .then(res => res.json())
    .then((summaryData) => {
        const dataList = document.getElementById('summary').getElementsByClassName('data');
        const change = summaryData['change'];
        const arrow = document.createElement('img');
        if (change > 0) {
            arrow.src = "https://csci571.com/hw/hw6/images/GreenArrowUp.jpg"
        } else if (change < 0){
            arrow.src = "https://csci571.com/hw/hw6/images/RedArrowDown.jpg"
        }

        dataList[0].innerHTML = summaryData['ticker'];
        dataList[1].innerHTML = summaryData['timestamp'];
        dataList[2].innerHTML = summaryData['prevClose'];
        dataList[3].innerHTML = summaryData['open'];
        dataList[4].innerHTML = summaryData['high'];
        dataList[5].innerHTML = summaryData['low'];
        dataList[6].innerHTML = change;
        dataList[7].innerHTML = summaryData['changePercent'];
        dataList[8].innerHTML = summaryData['volume'];

        if (change !== 0) {
            arrow.className = 'arrow-image';
            dataList[6].append(arrow);
            const arrowCopy = arrow.cloneNode(true);
            dataList[7].append(arrowCopy);
        }
    });
}

initChartsView = (ticker) => {
    fetch('/history/' + ticker)
        .then(res => res.json())
        .then((chartsData) => {
            let parsedDate = chartsData['date_array']
            let price = chartsData['price_array'];
            let volume = chartsData['volume_array'];
            parsedDate = parsedDate.map((x) => Date.parse(x));

            let stockPrice = []
            let stockVolume = []
            for (let i = 0; i < parsedDate.length; i++) {
                stockPrice.push([
                    parsedDate[i],
                    price[i]
                ]);
                stockVolume.push([
                    parsedDate[i],
                    volume[i]
                ]);
            }

            Highcharts.stockChart('charts', {
                title: {
                    text: 'Stock Price ' + tickerName + ' ' + chartsData['today_date']
                },

                subtitle: {
                    userHtml: true,
                    text: '<a href="https://api.tiingo.com/">Source: Tinngo</a>',
                },

                rangeSelector: {
                    buttons: [{
                        type: 'day',
                        count: 7,
                        text: '7d'
                    }, {
                        type: 'day',
                        count: 15,
                        text: '15d'
                    }, {
                        type: 'month',
                        count: 1,
                        text: '1m'
                    }, {
                        type: 'month',
                        count: 3,
                        text: '3m'
                    }, {
                        type: 'month',
                        count: 6,
                        text: '6m'
                    }],
                    selected: 4,
                    inputEnabled: false,
                },

                series: [{
                    name: tickerName,
                    type: 'area',
                    data: stockPrice,
                    tooltip: {
                        valueDecimals: 2
                    },
                    fillColor: {
                        linearGradient: {
                            x1: 0,
                            y1: 0,
                            x2: 0,
                            y2: 1
                        },
                        stops: [
                            [0, Highcharts.getOptions().colors[0]],
                            [1, Highcharts.color(Highcharts.getOptions().colors[0]).setOpacity(0).get('rgba')]
                        ]
                    },
                },
                {
                    type: 'column',
                    name: tickerName + ' Volume',
                    data: stockVolume,
                    yAxis: 1,
                    pointWidth: 2
                }],

                yAxis: [{
                    title: {
                        text: 'Stock Price'
                    },
                    opposite: false
                },{
                    labels: {
                        align: 'left'
                    },
                    title: {
                        text: 'Volume'
                    },
                    opposite: true
                }],
            });
        });

}

// fetch data
document.getElementById('search-form').addEventListener("submit", (e) => {
    e.preventDefault();
    const ticker = document.getElementById('search-bar').value;
    if (ticker.length === 0) {
        alert('invalid')
    }
    fetch('/outlook/' + ticker)
        .then(res => res.json())
        .then((data) => {
            if (data.hasOwnProperty('error')) {

            } else {
                initDefaultView(data);
                initSummaryView(ticker);
                initChartsView(ticker);
            }

        });
});




