https://dzone.com/articles/recommendation-engine-models


1. User based recomendation
"Similar users also bought" - recommendation.
1.1 Config
	ID		= user_rec
	EVENT	= order
	USERID	= userid
	ITEMID	= c_order_items
	TYPE	= user

2. Item based recomendation
"Items often bought together" - recommendation.
2.1 Config
	ID		= item_rec
	EVENT	= order
	USERID	= c_order_id
	ITEMID	= c_order_items
	TYPE	= item

3. Testdata
uid1 => item1, item2, item3
uid2 => item1, item2, item3
udi3 => item1, item3, item4
udi4 => item1, item3, item4
udi4 => item1, item3

http://localhost:8080/rest/extension?extension=recommendation-module&recommendation=user_rec&id=uid5&apikey=qi1khg1btjdqknl8ne47einbbb
=> item4, item2
http://localhost:8080/rest/extension?extension=recommendation-module&recommendation=item_rec&id=item1&apikey=qi1khg1btjdqknl8ne47einbbb
=> item4, item3, item2