import requests
import matplotlib.pyplot as plt
from PIL import Image
from io import BytesIO
import os
import pandas as pd
import numpy as np



def search(title):

	subscription_key = "15ceae79d8c4483d928ad4b80403255e"
	search_url = "https://api.cognitive.microsoft.com/bing/v7.0/images/search"
	search_term = title

	headers = {"Ocp-Apim-Subscription-Key" : subscription_key}

	params  = {"q": search_term,  "imageType": "photo"}

	response = requests.get(search_url, headers=headers, params=params)
	response.raise_for_status()
	search_results = response.json()
	thumbnail_urls = [img["thumbnailUrl"] for img in search_results["value"][:1]]
	if len(thumbnail_urls) < 1:
		return None
	thumbnail_urls = thumbnail_urls[0]
	image_data = requests.get(thumbnail_urls)
	image_data.raise_for_status()
	image = Image.open(BytesIO(image_data.content))   
	return image

exceptions = ['macdonald\'s', 'starbucks', '7-eleven', 'pizza hut', 'a&w', 'subway', 'tim horton']

def is_spec(name):
	for exception in exceptions:
		if exception in name.lower():
			return True
	return False

def spec_dir(name):
	for exception in exceptions:
		if exception in name.lower():
			exception_new = exception.replace(' ', '_')
			return 'images/' + exception_new + '.jpg'
	return None

data = pd.read_csv('restaurants.csv')
data['TRACKINGNUMBER'] = data['TRACKINGNUMBER'].apply(str.replace, args=(' ', ''))

data['spec'] = data['NAME'].apply(is_spec)
special_data = data[data['spec']]
special_data['dir'] = special_data['NAME'].apply(spec_dir)

output = special_data[['TRACKINGNUMBER', 'dir']]


download_data = data[~data['spec']]

def download(row):
	if not os.path.exists('images'):
		os.mkdir('images')

	image = search(row['NAME'])
	if image is None:
		print(row['NAME'], end=': ')
		print(row['TRACKINGNUMBER'])
		return

	dir = 'images/' + row['TRACKINGNUMBER'] + '.jpg'
	image.save(dir, quality=95)
	global output
	output = output.append({
		'TRACKINGNUMBER': row['TRACKINGNUMBER'],
		'dir': dir
	}, ignore_index=True)
	
download_data.apply(download, axis=1)
output.to_csv('dir.csv', header=False, index=False)

