"""
All these libraries are needed for script to work. Panda needs to be dowonloaded
and installed seperately.
Panda deals with the api conversion to a variable with json data
sqlite is needed for the connection to Data base
sequence Matcher is to compare "Name" fields in Data base and Api data
"""
import pandas as pd
import sqlite3
from difflib import SequenceMatcher

# variable json_data stores the information from api
url ="http://csgobackpack.net/api/GetItemsList/v2/"
json_data = pd.read_json(url)
#creating a array which will store needed values, from the json_data file.
rows = []

#this for loop iterates trough all 'items_list' items in json_data variable
for record in json_data['items_list']:
	#getting the name of current item.
	name = record['name']
	item_icon = record['icon_url']

	#variable valid is set to 0 as default
	valid = 0
	#if there is a gun name inside of name string, then we can set the valid to 1.
	if("AK-47" in name or "AWP" in name or "AUG" in name or "Dual Berettas" in name or "FAMAS" in name or "Five-SeveN" in name or "G3SG1" in name or "Galil AR" in name or "CZ75-Auto" in name or "Desert Eagle" in name or "Glock-18" in name or "M249" in name or "M4A1-S" in name or "M4A4" in name or "MAC-10" in name or "MAG-7" in name or "MP5-SD" in name or "MP7" in name or "MP9" in name or "Negev" in name or "Nova" in name or "P2000" in name or "P250" in name or "P90" in name or "PP-Bizon" in name or "R8 Revolver" in name or "Sawed-Off" in name or "SCAR-20" in name or "SG 553" in name or "SSG 08" in name or "Tec-9" in name or "UMP-45" in name or "USP-S " in name or "XM1014" in name):
		valid = 1
	#we still have to make sure that the item isn't any one of these item types, so if it is, the valid variable gets set back to 1.
	if("StatTrak" in name or "Souvenir" in name or "Graffiti" in name or "Sticker" in name or "Package" in name or "Patch" in name):
		valid = 0
	# if valid is 0, we know that current item isn't a weapon skin, so we skip the next steps and start a new iteration
	if (valid == 0):
		
		continue
	sep = '('
	split_name = name.split('(')
	name = split_name[0]
	name = name[:-1]
	wear = split_name[1]
	wear = wear[:-1]
	volume = 1
	#getting the price value from json data, we have to have exception, in case in json_data there is no price history in last 24 hours
	#and if there is no price history, then we can't update the prices, so we just continue to next item
	try:
		int(record['price']['24_hours']['sold'])>10
		if(int(record['price']['24_hours']['sold'])>10):
			try:
				price =record['price']['24_hours']['average']
			except:
				try:
					price =record['price']['7_days']['average']
				except:
					try:
						price =record['price']['30_days']['average']
					except:
						continue
	except:
		try:
			price =record['price']['7_days']['average']
		except:
			try:
				price =record['price']['30_days']['average']
			except:
				continue
	try:
		volume = record['price']['30_days']['sold']
	except:
		volume = 0
	
	"""
	In Api json data, the items wear is stored in the name of item, so to divide them split method has been used.
	The first half of split becomes the official name and second becomes the wear value.
	For both wear and the name variable, last characters are removed, since they are ) and blank respectively.
	"""
	
	
		
	#name, wear and price are pushed into rows array, making it 3 dimensional
	rows.append([name, wear, price, item_icon, volume])
	

#connecting to DB and defining a cursor
connection = sqlite3.connect('FinalSkinsDb.db')

cursor = connection.cursor()





def create_entry():
	#iterating trough all items we previously pushed into rows array.
	
	for d in rows:
		#for every item we need to open the database.
		icon_update = 'UPDATE skins SET IconUrl = ? WHERE Name = ? AND Condition=?'
		d[3]="https://steamcommunity-a.akamaihd.net/economy/image/"+d[3]
		icon_data = (d[3], d[0], d[1])	
		
		cursor.execute(icon_update, icon_data)
		connection.commit()
		#double checking that price value isn't 0
		if(d[2]!=0):
			#counter will be needed to know at what number we are while iterating trough data base,
			# so we would know what is the ID of item in database whose price we will update
			
			
			update_db = 'UPDATE skins SET PRICE = ?, Volume_30Days=?  WHERE Name = ? AND Condition=?'
			
			data = (d[2], d[4], d[0], d[1])
			#print(d)
			
			cursor.execute(update_db, data)
			connection.commit()
			
			
			
				

	#closing everything		
	cursor.close()
	connection.close()

#starting the update DB method
create_entry()
sys.close()


