import express from 'express';
import axios from 'axios';
import path from 'path';
import cors from 'cors';

const app = express();
const port = process.env.PORT || 3000;
const __dirname = path.resolve();

const TINNGO_API_KEY = 'ce4fc028989a34eaf097d57dfd14d50604c63cbd';
const NEWS_API_KEY = 'dfebb169ecb64e2ebb9823dcd0b60c44'

app.use(cors());
app.use(express.static(path.join(__dirname, 'dist')))

app.get('/api/outlook/:ticker', (req, res) => {
    const url = 'https://api.tiingo.com/tiingo/daily/' + req.params.ticker;
    axios.get(url, {
        params: {
            token: TINNGO_API_KEY
        }
    }).then(response => {
        res.json(response.data);
    }).catch(err => {
        if (err.response !== undefined && err.response.data !== undefined) {
            res.json({'error': 'Not Found'});
        } else {
            res.json({'error': 'error'});
            console.log("Cannot fetch company outlook with error " + err);
        }
    });
});

app.get('/api/price/:ticker', (req, res) => {
    const url = 'https://api.tiingo.com/iex/' + req.params.ticker;
    axios.get(url, {
        params: {
            token: TINNGO_API_KEY,
        }
    }).then(response => {
        const data = response.data
        let pricesArray = [];
        let entry;
        for (let i = 0; i < data.length; i++) {
            entry = data[i];
            const last = parseFloat(entry.last);
            const prevClose = parseFloat(entry.prevClose);
            let change;
            let changePercent;
            if (!isNaN(last) && !isNaN(prevClose)) {
                change = last - prevClose;
                let percentage = change * 100 / prevClose;
                change = parseFloat(change.toFixed(2));
                changePercent = parseFloat(percentage.toFixed(2));
            }
            pricesArray.push({
                'ticker': entry.ticker,
                'price': entry.last,
                'change': change,
                'changePercent': changePercent
            });
        }
        res.json(pricesArray);
    }).catch(err => {
        res.json([{'error': 'Not Found'}]);
        console.log("Cannot fetch stock latest price with error " + err);
    });
})

app.get('/api/summary/:ticker', (req, res) => {
    const url = 'https://api.tiingo.com/iex/' + req.params.ticker;
    axios.get(url, {
        params: {
            token: TINNGO_API_KEY,
        }
    }).then(response => {
        const data = response.data
        let tabularData = {}
        if (data.length > 0) {
            tabularData = data[0];
            const last = parseFloat(tabularData.last);
            const prevClose = parseFloat(tabularData.prevClose);
            if (!isNaN(last) && !isNaN(prevClose)) {
                let change = last - prevClose;
                let percentage = change * 100 / prevClose;
                tabularData.change = parseFloat(change.toFixed(2));
                tabularData.changePercent = parseFloat(percentage.toFixed(2));
            }
            if (tabularData.mid === null) {
                tabularData.mid = "-";
            }

            const parsedCurrentTime = Date.parse(new Date());
            const parsedStockTime = Date.parse(tabularData.timestamp);

            tabularData.marketOpen = parsedCurrentTime - parsedStockTime < 60000 ? true : false;
            tabularData.todayDate = parsedCurrentTime;
            tabularData.timestamp = parsedStockTime;
        }
        res.json(tabularData);
    }).catch(err => {
        res.json({'error': 'Not Found'});
        console.log("Cannot fetch company summary with error " + err);
    });
});

app.get('/api/history/:ticker', (req, res) => {
    const url = 'https://api.tiingo.com/tiingo/daily/' + req.params.ticker + '/prices';
    const twoYearsAgo = new Date(new Date().setFullYear(new Date().getFullYear() - 2)).toISOString().split('T')[0];

    axios.get(url, {
        params: {
            token: TINNGO_API_KEY,
            startDate: twoYearsAgo,
            resampleFreq: 'daily',
        }
    }).then(response => {
        const data = response.data;
        const historyDate = [];
        const openPrice = [];
        const highPrice = [];
        const lowPrice = [];
        const closePrice = [];
        const volume = [];
        for (let i = 0; i < data.length; i++) {
            historyDate.push(Date.parse(data[i].date.split('T')[0]));
            openPrice.push(data[i].open);
            highPrice.push(data[i].high);
            lowPrice.push(data[i].low);
            closePrice.push(data[i].close);
            volume.push(data[i].volume);
        }
        res.json({
            'date_array': historyDate,
            'open_price_array': openPrice,
            'high_price_array': highPrice,
            'low_price_array': lowPrice,
            'close_price_array': closePrice,
            'volume_array': volume,
        });
    }).catch(err => {
        res.json({'error': 'Not Found'});
        console.log("Cannot fetch company stock history with error " + err);
    });
});

app.get('/api/last/:ticker', (req, res) => {
    const url = 'https://api.tiingo.com/iex/' + req.params.ticker + '/prices';
    axios.get(url, {
        params: {
            token: TINNGO_API_KEY,
            startDate: req.query.lastTimeStamp,
            resampleFreq: '4min',
            columns: 'open,high,low,close,volume'
        }
    }).then(response => {
        const data = response.data;
        const historyDate = [];
        const historyPrice = [];
        for (let i = 0; i < data.length; i++) {
            historyDate.push(Date.parse(data[i].date));
            historyPrice.push(data[i].close);
        }
        res.json({
            'date_array': historyDate,
            'price_array': historyPrice,
        });
    }).catch(err => {
        res.json({'error': 'Not Found'});
        console.log("Cannot fetch company last day data with error " + err);
    });
});

app.get('/api/search/:query', (req, res) => {
    const url = 'https://api.tiingo.com/tiingo/utilities/search';
    axios.get(url, {
        params: {
            token: TINNGO_API_KEY,
            query: req.params.query
        }
    }).then(response => {
        const data = response.data;
        const tabularData = []
        for (let i = 0; i < data.length; i++) {
            tabularData.push({
                'ticker': data[i].ticker,
                'name': data[i].name
            });
        }
        res.json(tabularData);
    }).catch(err => {
        console.log("Cannot fetch company's list with error " + err);
    })
})

app.get('/api/news/:ticker', (req, res) => {
    const url = 'https://newsapi.org/v2/everything';
    axios.get(url, {
        params: {
            q: req.params.ticker,
            apiKey: NEWS_API_KEY
        }
    }).then(response => {
        const articles = response.data.articles;
        const topTwentyNews = [];
        if (articles !== undefined) {
            for (let i = 0; i < articles.length; i++) {
                const url = articles[i].url;
                const title = articles[i].title;
                const description = articles[i].description;
                const source = articles[i].source;
                const urlToImage = articles[i].urlToImage;
                const publishedAt = articles[i].publishedAt;

                if (url != null && title != null && description != null && source != null 
                    && urlToImage != null && publishedAt != null && source.name != null) {
                        let d = new Date(Date.parse(publishedAt));
                        let dateArray = d.toLocaleDateString().split('/');
                        let date = d.toLocaleString('default', { month: 'long' }) + ' ' + dateArray[1] + ', ' + dateArray[2];

                        topTwentyNews.push({
                            'url': url,
                            'title': title,
                            'description': description,
                            'source': source,
                            'urlToImage': urlToImage,
                            'publishedAt': date,
                        });
                }

                if (topTwentyNews.length >= 20) {
                    break;
                }
            }
        }
        res.json(topTwentyNews);
    }).catch(err => {
        console.log("Cannot fetch company summary with error " + err);
    });
});

app.get('*', (req, res) => {
    res.sendFile(path.join(__dirname, 'dist/index.html'));
});

app.listen(port);