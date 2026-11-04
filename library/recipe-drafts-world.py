"""Original international-inspired drafts; not authentic standards or clinical prescriptions.
Budget tiers are editorial procurement hypotheses, never verified local prices.
All weights cover the whole two-serving recipe, not one serving.
"""
RECIPES=[]
def add(n, az, en, ru, cuisine, tier, group, minutes, ingredients, steps):
    RECIPES.append(dict(id=f'G{n:02}', names=dict(az=az,en=en,ru=ru), group=group,
        servings=2, estimated_minutes=minutes, ingredients=ingredients, steps_az=steps,
        discovery=dict(cuisine_inspiration=cuisine,budget_tier_candidate=tier,
          budget_status='editorial_hypothesis_requires_region_price_check',
          goal_matching='evaluate_portion_and_complete_day_under_approved_policy',
          evidence_refs=['S2','S3','S14','S15','S16'],
          evidence_scope='ingredient_data_and_dietary_pattern_not_recipe_clinical_trial')))
add(1,'Noxudlu şakşuka','Chickpea shakshuka','Шакшука с нутом','North Africa / Middle East','low','breakfast',30,
 dict(egg=200,chickpeas_cooked=160,tomato=350,onion=80,red_pepper=150,olive_oil=8,cumin=2,salt=1),
 ['Soğan və bibəri doğrayıb yağda yumşalt, pomidor və zirəni əlavə et.','Bişmiş noxudu qatıb sousu qatılaşdır.','Yumurtaları sousa qır, qapaq altında ağı və sarısı tam bərkiyənədək bişir; iki porsiyaya böl.'])
add(2,'Mərciməkli mücəddərə','Lentil rice mujaddara','Муджаддара с чечевицей','Levant','low','plant_main',45,
 dict(lentils_cooked=300,brown_rice=100,onion=200,olive_oil=12,cumin=2,water=350,salt=1),
 ['Quru qəhvəyi düyünü su ilə tam yumşalana qədər bişir; lazım olduqda su əlavə et.','Doğranmış soğanı ölçülmüş yağda zəif odda yumşalt.','Əvvəlcədən bişmiş mərcimək, düyü, zirə və duzu qatıb tam qızdır və böl.'])
add(3,'Ağ lobyalı ribollita üslubunda şorba','White bean vegetable soup','Овощной суп с белой фасолью','Italy','low','soup',35,
 dict(white_beans_cooked=300,cabbage=180,carrot=100,tomato=200,whole_bread=80,olive_oil=10,garlic=6,water=600,salt=1),
 ['Kök, kələm və sarımsağı doğrayıb yağda qısa yumşalt.','Su, pomidor və əvvəlcədən bişmiş lobyanı əlavə edib tərəvəzlər yumşalanadək bişir.','Çörəyi kiçik doğrayıb şorbaya qat, bir neçə dəqiqə tam qızdır və iki qaba böl.'])
add(4,'Qara lobyalı takos kasası','Black bean taco bowl','Тако-боул с чёрной фасолью','Mexico','low','plant_main',25,
 dict(black_beans_cooked=300,brown_rice_cooked=220,corn_cooked=100,tomato=180,red_cabbage=120,lime=40,olive_oil=8,cumin=2),
 ['Bişmiş lobya, bişmiş düyü və qarğıdalını zirə ilə tam qızdır.','Kələm və pomidoru yuyub doğra; laymın yeyilən lətini xırdala.','Bütün ərzaqları iki kasaya böl, yağı ölçərək əlavə et.'])
add(5,'Qatıq və tahinli noxud kasası','Yogurt tahini chickpea bowl','Нут с йогуртом и тахини','Levant','medium','plant_main',15,
 dict(chickpeas_cooked=320,yogurt=180,tahini=30,cucumber=200,whole_bread=100,lemon_juice=20,garlic=4),
 ['Xiyarı yuyub doğra, bişmiş noxudu istifadəyə hazır vəziyyətə gətir.','Qatıq, tahin, limon şirəsi və əzilmiş sarımsağı qarışdır.','Çörək, noxud, xiyar və sousu iki porsiyaya böl; noxud soyuq istifadə olunursa təhlükəsiz soyudulmuş olmalıdır.'])
add(6,'Zəncəfilli tofu və qarabaşaq','Ginger tofu buckwheat bowl','Тофу с имбирём и гречкой','East Asia inspired','medium','plant_main',30,
 dict(tofu=300,buckwheat=100,broccoli=250,carrot=100,ginger=10,sesame_oil=8,sesame=10,water=300,salt=1),
 ['Quru qarabaşağı suda yumşalanadək bişir.','Tofunu doğra, yağda qızdır; zəncəfil, kök və brokkolini əlavə edib tam bişir.','Duzu və küncütü qat, qarabaşaqla iki porsiyaya böl.'])
add(7,'Qırmızı mərciməkli dal və düyü','Red lentil dal with rice','Дал из красной чечевицы с рисом','India inspired','low','plant_main',35,
 dict(red_lentils=130,rice=80,tomato=200,onion=80,turmeric=2,cumin=2,canola_oil=8,water=850,salt=1),
 ['Mərcimək və düyünü ayrı yuyub hərəsini ayrılmış suyun uyğun hissəsində yumşalanadək bişir.','Soğanı yağda yumşalt, pomidor və ədviyyatı əlavə et.','Mərciməyi sousa qatıb tam bişir; düyü ilə iki porsiyaya böl, lazım gəlsə su əlavə et.'])
add(8,'Sardinalı pomidor makaronu','Sardine tomato pasta','Паста с сардинами и томатами','Mediterranean','low','main',25,
 dict(whole_pasta=140,sardine=150,tomato=250,spinach=120,olive_oil=6,garlic=5),
 ['Makaronu suda paket təlimatına uyğun bişirib süz; suyun çoxu yeyilmir və hesabın məhdudiyyəti kimi qeyd olunur.','Pomidor və sarımsağı yağda bişir, ispanağı yumşalt.','Konserv sardinanın bazadakı yeyilən hissəsinə uyğun miqdarını və makaronu əlavə edib tam qızdır, böl.'])
add(9,'Somonlu kinoa və brokkoli','Salmon quinoa broccoli bowl','Лосось с киноа и брокколи','International','high','main',35,
 dict(salmon=280,quinoa=110,broccoli=300,olive_oil=10,lemon_juice=25,water=330,salt=1),
 ['Kinoanı yuyub suda tam yumşalanadək bişir.','Somonu yağ və limonla sobada mərkəzi minimum 63°C olana qədər bişir.','Brokkolini buğda yumşalt, bütün komponentləri iki porsiyaya böl.'])
add(10,'Toyuqlu arpa risottosu','Chicken barley risotto','Перловое ризотто с курицей','Italy inspired','medium','main',55,
 dict(chicken=280,barley=120,mushroom=200,onion=70,olive_oil=10,parmesan=20,water=650,salt=1),
 ['Soğan və göbələyi doğra, yağda yumşalt.','Quru arpa, doğranmış toyuq və suyu əlavə et, lazım gəlsə su artıraraq arpa yumşalanadək bişir.','Toyuğun mərkəzinin minimum 74°C olduğunu yoxla, pendiri qatıb iki porsiyaya böl.'])
add(11,'Yunan üslubunda limonlu lobya','Greek inspired lemon beans','Белая фасоль с лимоном по-гречески','Greece','low','plant_main',30,
 dict(white_beans_cooked=350,tomato=250,carrot=120,onion=80,olive_oil=12,lemon_juice=25,oregano=2,whole_bread=80,water=150),
 ['Soğan və kökü doğrayıb yağda yumşalt.','Pomidor, bişmiş lobya, su və oreqanonu qatıb tərəvəzlər yumşalanadək bişir.','Limon şirəsini əlavə et; ölçülmüş çörəklə iki porsiyaya böl.'])
add(12,'Balqabaqlı noxud kuskusu','Pumpkin chickpea couscous','Кускус с тыквой и нутом','North Africa inspired','low','plant_main',30,
 dict(couscous=110,chickpeas_cooked=240,pumpkin=300,carrot=100,olive_oil=10,cumin=2,cinnamon=1,water=350,salt=1),
 ['Balqabaq və kökü doğrayıb yağ, zirə və suyun bir hissəsi ilə yumşalanadək bişir.','Bişmiş noxud və darçını qatıb tam qızdır.','Kuskusu paketə uyğun ayrılmış isti suda şişirt; tərəvəzli noxudla iki porsiyaya böl.'])
add(13,'Almalı gecə yulafı və yerfındığı','Apple peanut overnight oats','Ночная овсянка с яблоком и арахисом','International','low','breakfast',10,
 dict(oats=100,milk_2=300,yogurt=150,apple=200,peanut_butter=30,cinnamon=1),
 ['Yulaf, pasterizə süd, qatıq, darçın və yerfındığı pastasını qarışdır.','Qapalı qabda dərhal soyuducuya qoy; hazırlama vaxtına soyutma daxil deyil.','Servisdə yuyulub doğranmış almanı qat və iki porsiyaya böl; saxlanma təlimatı ayrıca yoxlanmalıdır.'])
add(14,'Bananlı tahinli yoqurt kasası','Banana tahini yogurt bowl','Йогурт с бананом и тахини','International','medium','breakfast',10,
 dict(greek_yogurt=300,banana=220,oats=70,tahini=35,dates=40),
 ['Bananı soy, çəyirdəksiz xurmanı xırdala; çəkilər yeyilən hissəyə aiddir.','Yoqurt və tahini qarışdır, yulafı əlavə et.','Meyvələri üstünə böl və iki porsiya hazırla.'])
add(15,'Yumurtalı kartof və kələm tavası','Egg potato cabbage skillet','Картофельно-капустная сковорода с яйцом','Central Europe inspired','low','main',35,
 dict(egg=200,potato=350,cabbage=250,onion=70,canola_oil=10,paprika=2,water=120,salt=1),
 ['Kartof, kələm və soğanı doğrayıb yağ və su ilə qapaq altında yumşalt.','Paprika və duzu qat, suyun artığını buxarlandır.','Çalınmış yumurtanı əlavə et, ağı və sarısı tam bərkiyənədək bişir; iki porsiyaya böl.'])
add(16,'İspan üslubunda ispanaqlı noxud','Spanish inspired spinach chickpeas','Нут со шпинатом по-испански','Spain','low','plant_main',20,
 dict(chickpeas_cooked=350,spinach=250,tomato=180,garlic=8,olive_oil=12,paprika=2,whole_bread=90),
 ['Sarımsağı yağda yüngül yumşalt, pomidor və paprikanı qat.','Bişmiş noxud və yuyulmuş ispanağı əlavə edib tam bişir.','Çörəyi ayrıca ölçüb yeməklə iki porsiyaya böl.'])
add(17,'Zəncəfilli toyuq və qəhvəyi düyü','Ginger chicken rice bowl','Курица с имбирём и бурым рисом','East Asia inspired','medium','main',45,
 dict(chicken=300,brown_rice=110,napa_cabbage=220,carrot=120,ginger=12,sesame_oil=8,water=380,salt=1),
 ['Qəhvəyi düyünü suda tam yumşalanadək bişir.','Toyuğu doğrayıb yağda bişir, zəncəfil və tərəvəzləri əlavə et.','Toyuq mərkəzinin minimum 74°C olduğunu yoxla; düyü və duzla iki porsiyaya böl.'])
add(18,'Badamlı şirin kartof və mərcimək','Almond sweet potato lentils','Чечевица с бататом и миндалём','International','medium','plant_main',40,
 dict(sweet_potato=320,lentils_cooked=300,spinach=120,almond=25,olive_oil=10,lemon_juice=25,cumin=2),
 ['Şirin kartofu doğrayıb yağ və zirə ilə sobada tam yumşalt.','Bişmiş mərcimək və ispanağı ayrıca tam qızdır.','Limon şirəsi və xırdalanmış badamla komponentləri iki porsiyaya böl.'])
add(19,'Forelli bulqur və göyərti','Trout herb bulgur bowl','Форель с булгуром и зеленью','Mediterranean','high','main',35,
 dict(trout=300,bulgur=120,zucchini=250,parsley=25,lemon_juice=25,olive_oil=10,water=350,salt=1),
 ['Bulquru suda tam yumşalanadək bişir.','Foreli və doğranmış qabağı yağla sobada bişir; balığın mərkəzi minimum 63°C olmalıdır.','Limon və yuyulmuş cəfərini qat, sümükləri ayıraraq iki porsiyaya böl; balıq çəkisi yeyilən hissədir.'])
add(20,'Krevetli avokado və düyü kasası','Shrimp avocado rice bowl','Рисовый боул с креветками и авокадо','International','high','main',20,
 dict(shrimp_cooked=260,rice_cooked=260,avocado=140,cucumber=180,red_cabbage=100,lime=40,sesame=12),
 ['Hazır bişmiş krevet və düyünü təhlükəsiz saxlanmış halda götür, servis üçün tam qızdır.','Xiyar, kələm, avokado və laymın yeyilən lətini doğra.','İki kasaya böl və küncütü səp; krevet qabıq çəkisi daxil deyil.'])
add(21,'Kakao və yerfındıqlı süd içkisi','Cocoa peanut milk smoothie','Молочный смузи с какао и арахисом','International','low','snack',10,
 dict(milk_whole=400,banana=200,peanut_butter=35,cocoa=8,oats=40),
 ['Bananı soyub doğra, pasterizə südü ölç.','Bütün inqrediyentləri blenderdə hamar qarışdır.','İki porsiyaya böl və dərhal servis et; əsas yemək əvəzi kimi avtomatik seçilmir.'])
add(22,'Ağ lobyalı limonlu çörəküstü','Lemon white bean toast','Тост с белой фасолью и лимоном','Italy inspired','low','snack',15,
 dict(white_beans_cooked=220,whole_bread=100,lemon_juice=20,olive_oil=8,parsley=15,tomato=120),
 ['Bişmiş lobyanı limon və ölçülmüş yağla əz.','Çörəyi yüngül qızart, pomidor və cəfərini yuyub doğra.','Lobya əzməsi və tərəvəzləri çörəyin üstünə bölərək iki porsiya hazırla.'])
add(23,'Rikotta, fındıq və əncir kasası','Ricotta hazelnut fig bowl','Рикотта с фундуком и инжиром','Mediterranean','high','snack',10,
 dict(ricotta=200,fig=200,hazelnut=30,oats=30),
 ['Ənciri yuyub doğra, fındığı xırdala.','Rikotta və yulafı qarışdır.','Meyvə və fındığı əlavə edib iki porsiyaya böl.'])
add(24,'Göbələkli mərcimək bolonyezi','Mushroom lentil bolognese','Болоньезе из чечевицы и грибов','Italy inspired','low','plant_main',35,
 dict(lentils_cooked=280,whole_pasta=130,mushroom=220,tomato=300,onion=70,olive_oil=10,garlic=6,oregano=2),
 ['Makaronu paket təlimatına uyğun suda bişirib süz; suyun çoxu yeyilmir.','Soğan, göbələk və sarımsağı yağda yumşalt, pomidor və oreqanonu əlavə edib bişir.','Bişmiş mərciməyi sousda tam qızdır, makaronla iki porsiyaya böl.'])
