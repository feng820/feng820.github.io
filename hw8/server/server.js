import express from 'express';
import axios from 'axios';
import path from 'path';

const app = express();
const port = 3000;
const __dirname = path.resolve();

const TINNGO_API_KEY = 'ce4fc028989a34eaf097d57dfd14d50604c63cbd';
const NEWS_API_KEY = 'dfebb169ecb64e2ebb9823dcd0b60c44'

app.get('/', (req, res) => {
    //res.sendFile(path.join(__dirname, '../client/index.html'));
    res.send("Hello Hello World");
});

app.get('/outlook/:ticker', (req, res) => {
    const url = 'https://api.tiingo.com/tiingo/daily/' + req.params.ticker;
    axios.get(url, {
        params: {
            token: TINNGO_API_KEY
        }
    }).then(response => {
        res.json(response.data);
    }).catch(err => {
        if (err.response.data.detail != undefined) {
            res.json({'error': 'Not Found'});
        } else {
            console.log("Cannot fetch company outlook with error " + err);
        }
    });
});

app.get('/summary/:ticker', (req, res) => {
    const url = 'https://api.tiingo.com/iex/' + req.params.ticker;
    axios.get(url, {
        params: {
            token: TINNGO_API_KEY
        }
    }).then(response => {
        const data = response.data
        let tabularData = {}
        if (data.length > 0) {
            tabularData = data[0];
        }
        res.json(tabularData);
    }).catch(err => {
        console.log("Cannot fetch company summary with error " + err);
    });
});

app.get('/history/:ticker', (req, res) => {
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
        const historyPrice = [];
        const historyVolume = [];
        for (let i = 0; i < data.length; i++) {
            historyDate.push(Date.parse(data[i].date.split('T')[0]));
            historyPrice.push(data[i].close);
            historyVolume.push(data[i].volume);
        }
        res.json({
            'date_array': historyDate,
            'price_array': historyPrice,
            'volume_array': historyVolume,
        });
    }).catch(err => {
        console.log("Cannot fetch company stock history with error " + err);
    });
});

app.get('/last/:ticker', (req, res) => {
    const url = 'https://api.tiingo.com/iex/' + req.params.ticker + '/prices';
    axios.get(url, {
        params: {
            token: TINNGO_API_KEY,
            startDate: req.query.lastTimeStamp,
            resampleFreq: '1hour',
            columns: 'open,high,low,close,volume'
        }
    }).then(response => {
        const data = response.data;
        const historyDate = [];
        const historyPrice = [];
        const historyVolume = [];
        for (let i = 0; i < data.length; i++) {
            historyDate.push(Date.parse(data[i].date));
            //historyDate.push(data[i].date);
            historyPrice.push(data[i].close);
            historyVolume.push(data[i].volume);
        }
        res.json({
            'date_array': historyDate,
            'price_array': historyPrice,
            'volume_array': historyVolume,
        });
    }).catch(err => {
        console.log("Cannot fetch company last day data with error " + err);
    });
});

app.get('/search/:query', (req, res) => {
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

app.get('/news/:ticker', (req, res) => {
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
                topTwentyNews.push({
                    'url': articles[i].url,
                    'title': articles[i].title,
                    'description': articles[i].description,
                    'source': articles[i].source,
                    'urlToImage': articles[i].urlToImage,
                    'publishedAt': articles[i].publishedAt,
                });
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

app.listen(port);