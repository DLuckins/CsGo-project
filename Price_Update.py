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

	#getting the price value from json data, we have to have exception, in case in json_data there is no price history in last 24 hours
	#and if there is no price history, then we can't update the prices, so we just continue to next item
	try:
		price =record['price']['24_hours']['average']
	except:
		continue
	"""
	In Api json data, the items wear is stored in the name of item, so to divide them split method has been used.
	The first half of split becomes the official name and second becomes the wear value.
	For both wear and the name variable, last characters are removed, since they are ) and blank respectively.
	"""
	sep = '('
	split_name = name.split('(')
	name = split_name[0]
	name = name[:-1]
	wear = split_name[1]
	wear = wear[:-1]
	
		
	#name, wear and price are pushed into rows array, making it 3 dimensional
	rows.append([name, wear, price])
	

#connecting to DB and defining a cursor
connection = sqlite3.connect('FinalSkinsDb.db')

cursor = connection.cursor()





def create_entry():
	#iterating trough all items we previously pushed into rows array.
	for d in rows:
		#for every item we need to open the database.
		cursor.execute('SELECT * FROM skins')
		#double checking that price value isn't 0
		if(d[2]!=0):
			#counter will be needed to know at what number we are while iterating trough data base,
			# so we would know what is the ID of item in database whose price we will update
			counter =0;
			#Starting the loop, iterating trough whole database until we find a match for our item.
			for DBrows in cursor.fetchall():
				counter = counter + 1
				
				#Since some items names have some random characters when using the API data, direct comparision between items name and name in database -
				#wont work, so this function is used to allow, some not 100% matches go trough.
				if(SequenceMatcher(a=d[0],b=DBrows[1]).ratio()>0.9 and d[1] == DBrows[2]):
					#this is the sql lite command, stored in update_dp variable. 
					#Variable data stores the information needed in update_dp '?' simbols.
					update_db = 'UPDATE Skins SET Price= ? WHERE Id =?'
					data = (d[2], counter)
					#Executing and commiting the update to the data base
					cursor.execute(update_db, data)
					connection.commit()
					#If we've found the correct entry in database and updated, there is no need to iterate trough the remaining database,
					#we can just skip to next item.
					break
				
				
	#closing everything		
	cursor.close()
	connection.close()

#starting the update DB method
create_entry()


