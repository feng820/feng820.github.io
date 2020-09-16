from flask import Flask
from datetime import date
from dateutil.relativedelta import relativedelta
import requests
import json

TIINGO_API_KEY = 'ce4fc028989a34eaf097d57dfd14d50604c63cbd'
NEWS_API_KEY = 'dfebb169ecb64e2ebb9823dcd0b60c44'

# EB looks for an 'application' callable by default.
application = Flask(__name__)


@application.route('/')
def index():
    return application.send_static_file('index.html')


@application.route('/outlook/<ticker>')
def get_company_outlook(ticker):
    target_url = 'https://api.tiingo.com/tiingo/daily/' + ticker + '?token=' + TIINGO_API_KEY
    json_response = requests.get(target_url).content
    data = json.loads(json_response)
    error_msg = data.get('detail')
    if error_msg:
        return json.dumps({'error': error_msg})
    else:
        tabular_data = {'name': data.get('name'),
                        'ticker': data.get('ticker'),
                        'exchangeCode': data.get('exchangeCode'),
                        'startDate': data.get('startDate'),
                        'description': data.get('description')}
        return json.dumps(tabular_data)


@application.route('/summary/<ticker>')
def get_stock_summary(ticker):
    target_url = 'https://api.tiingo.com/iex/' + ticker + '?token=' + TIINGO_API_KEY
    json_response = requests.get(target_url).content
    data = json.loads(json_response)
    tabular_data = {}
    if len(data) > 0:
        data = data[0]
        open_price = data.get('open')
        last_price = data.get('last')
        # TODO: round??
        change = round(last_price - open_price, 2)
        tabular_data = {'ticker': data.get('ticker'),
                        'timestamp': data.get('timestamp').split('T')[0],
                        'prevClose': data.get('prevClose'),
                        'open': open_price,
                        'high': data.get('high'),
                        'low': data.get('low'),
                        'change': change,
                        'changePercent': round(abs(change) / open_price * 100, 2),
                        'volume': data.get('volume'),
                        }
    return json.dumps(tabular_data)


@application.route('/history/<ticker>')
def get_stock_history(ticker):
    six_months_ago = date.today() + relativedelta(months=-6)
    target_url = 'https://api.tiingo.com/iex/' + ticker + '/prices' + '?startDate=' + str(six_months_ago) + \
                 '&resampleFreq=12hour&columns=open,high,low,close,volume' + '&token=' + TIINGO_API_KEY
    json_response = requests.get(target_url).content
    data = json.loads(json_response)
    date_array = []
    price_array = []
    volume_array = []
    for history in data:
        history_date = history.get('date').split('T')[0]
        stock_price = history.get('close')
        volume = history.get('volume')
        date_array.append(history_date)
        price_array.append(stock_price)
        volume_array.append(volume)
    tabular_data = {'today_date': str(date.today()),
                    'date_array': date_array,
                    'price_array': price_array,
                    'volume_array': volume_array,
                    }
    return json.dumps(tabular_data)


@application.route('/news/<ticker>')
def get_stock_news(ticker):
    target_url = 'https://newsapi.org/v2/everything?q=' + ticker + '&apiKey=' + NEWS_API_KEY
    json_response = requests.get(target_url).content
    data = json.loads(json_response)

    top_five_articles = []
    try:
        articles = data['articles']
        for article in articles:
            if article.get('title') and article.get('url') and article.get('urlToImage') and article.get('publishedAt'):
                tabular_data = {
                    'image': article.get('urlToImage'),
                    'title': article.get('title'),
                    'date': article.get('publishedAt'),
                    'url': article.get('url')
                }
                top_five_articles.append(tabular_data)
            if len(top_five_articles) == 5:
                break
    except KeyError:
        err_msg = {'error': 'Not found'}
        return json.dumps(err_msg)
    return json.dumps(top_five_articles)


# run the app.
if __name__ == "__main__":
    application.run()
