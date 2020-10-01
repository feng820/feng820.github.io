addEventListenerForButton = (id) => {
    // press the button will add border to it
    // dismiss the border on the next mouse click
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

clearAll = (keepInput=false) => {
    if (!keepInput) {
        document.getElementById('search-bar').value = "";
    }
    document.getElementById('tab-options').style.display = 'none';
    document.getElementById('error').style.display = 'none';
    const allTabContent = document.getElementsByClassName('tab-content')
    for (let i = 0; i < allTabContent.length; i++) {
        allTabContent[i].style.display = "none";
    }
}

showWarningBox = () => {
    const warningBox = document.getElementById('warning-box')
    const searchBar = document.getElementById('search-bar');
    warningBox.style.display = 'block';
    searchBar.focus();

    // next mouse click or keyboard input will dismiss the warning
    searchBar.addEventListener('keydown', (e) => {
        warningBox.style.display = 'none';
    }, {once: true});
    document.addEventListener('mousedown', (e) => {
        warningBox.style.display = 'none';
    }, {once: true})
}

// fetch data
document.getElementById('search-form').addEventListener("submit", (e) => {
    e.preventDefault();
    const ticker = document.getElementById('search-bar').value;
    clearAll(true);
    fetch('/outlook/' + ticker)
        .then(res => res.json())
        .then((data) => {
            if (data.hasOwnProperty('error')) {
                document.getElementById('error').style.display = 'block';
            } else {
                document.getElementById('error').style.display = 'none';
                initDefaultView(data);
                initSummaryView(ticker);
                initChartsView(ticker);
                initNewsView(ticker);
            }
        });
});

initDefaultView = (data) => {
    tickerName = data['ticker'];
    document.getElementById('tab-options').style.display = 'block';
    changeTab('outlook', 0);
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
        dataList[6].innerHTML = summaryData['last'];
        dataList[7].innerHTML = change
        dataList[8].innerHTML = summaryData['changePercent'].length !== 0 ? summaryData['changePercent'] + '%' : '';
        dataList[9].innerHTML = summaryData['volume'];

        if (change !== 0) {
            arrow.className = 'arrow-image';
            dataList[7].append(arrow);
            const arrowCopy = arrow.cloneNode(true);
            dataList[8].append(arrowCopy);
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
                    text: 'Stock Price ' + tickerName + ' ' + chartsData['today_date'],
                    margin: 25
                },

                subtitle: {
                    text: '<a href="https://api.tiingo.com/">Source: Tinngo</a>',
                    y: 40
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
                    allButtonsEnabled: true,
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

                plotOptions: {
                    series: {
                        pointPlacement: 'on'
                    }
                },

                // xAxis: {
                //     tickPixelInterval: 90,
                //     type: 'datetime',
                //     minRange: 7 * 24 * 360000,
                // },

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

            const subTitle = document.querySelector('.highcharts-subtitle a')
            subTitle.addEventListener('click', (e) => {
                e.preventDefault();
                window.open('https://api.tiingo.com/', '_blank');
            });
        });

}

initNewsView = (ticker) => {
    fetch('/news/' + ticker)
    .then(res => res.json())
    .then((newsData) => {
        if (!newsData.hasOwnProperty('error')) {
            const tab = document.getElementById('news');
            const ul = document.createElement('ul');
            tab.innerHTML = "";
            tab.appendChild(ul);
            let li, img, div, title, link
            for (let i = 0; i < newsData.length; i++) {
                li = document.createElement('li');
                img = document.createElement('img');
                div = document.createElement('div');
                title = document.createElement('b');
                link = document.createElement('a');

                const article = newsData[i];
                let UTC = article['date'];
                UTC = UTC.split('T')[0];
                UTC = UTC.split('-');
                const date = UTC[1] + '/' + UTC[2] + '/' + UTC[0];

                img.src = article['image'];
                link.href = article['url'];
                link.style.textDecoration = 'underline';
                link.target = '_blank';
                title.innerHTML = article['title'];
                div.appendChild(title);
                div.innerHTML += "<br/>"
                div.innerHTML += "Published Date: " + date;
                div.innerHTML += "<br/>"
                link.innerHTML = "See Original Post"
                div.appendChild(link);
                li.appendChild(img);
                li.appendChild(div);
                ul.appendChild(li);
            }
        }
    });

}
