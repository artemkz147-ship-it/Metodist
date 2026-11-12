export const MAX_PYTHON_LINES = 15;

export const WOW_EXAMPLES = [
  {id:'fireworks',title:'Управляемый салют',level:'easy',category:'visual',theme:'particles',output:'canvas',tag:'math · частицы',color:'#ff5f6d',brief:'Создай взрыв из 120 частиц, которые летят во все стороны.',hints:['Подключи math и wow','Угол частицы: i * 2π / 120','Скорость передай в vx и vy'],code:`from wow import *
import math
background("#05010f","#16072d")
color="#ff5f9e"
count=120
size=3
speed=1.0
glow=18
for i in range(count):
    a=i*math.tau/count
    v=(2+i%9)*speed
    particle(450,280,color,size,glow,math.cos(a)*v,math.sin(a)*v)
orb(450,280,18,"#fff",30)
title("PYRO","салют из Python")
bloom(1.4)`},
  {id:'ocean',title:'Неоновый океан',level:'easy',category:'visual',theme:'nature',output:'canvas',tag:'sin · волны',color:'#43c9ff',brief:'Нарисуй шесть живых волн с помощью синуса.',hints:['Собирай точки в список','y зависит от math.sin(x)','Каждой волне дай свой цвет'],code:`from wow import *
import math
background("#04101d","#172957")
color="#43c9ff"; size=6; speed=1.0
for band in range(6):
    points=[]
    for x in range(0,901,15):
        y=250+band*34+math.sin(x*.018+band)*28
        points.append((x,y))
    wave(points,palette(band,[color,"#45e6b5","#8b5cf6"]),size,speed)
sun(710,125,70,"#ff8b6d")
title("NEON TIDE","синус стал морем")`},
  {id:'snow',title:'Снежная ночь',level:'easy',category:'visual',theme:'weather',output:'canvas',tag:'random · animation',color:'#dffcff',brief:'Сделай снег разного размера и скорости.',hints:['Используй random.randint','Координаты должны быть случайными','Скорость передаётся как vy'],code:`from wow import *
import random
random.seed(9)
background("#06101d","#15345a")
color="#dffcff"; count=180; max_size=5; speed=1.2
for i in range(count):
    x=random.randint(0,900); y=random.randint(0,560)
    size=random.randint(1,max_size)
    particle(x,y,color,size,10,vy=random.random()*speed)
orb(760,110,54,"#e9fbff",25)
title("WINTER","снег создан циклом")`},
  {id:'mandala',title:'Живая мандала',level:'medium',category:'visual',theme:'generative',output:'canvas',tag:'geometry · symmetry',color:'#ffc857',brief:'Построй симметричный узор из окружностей и частиц.',hints:['Используй math.tau','x и y считай через cos и sin','Добавь восемь лепестков'],code:`from wow import *
import math
background("#090410","#231038")
color="#ffc857"; n=96; scale=1.0; speed=.5
for i in range(n):
    a=i*math.tau/n
    r=85+55*math.sin(a*8)
    x=450+math.cos(a)*r*scale
    y=280+math.sin(a)*r*scale
    particle(x,y,palette(i,[color,"#ff5f9e","#8b5cf6"]),3+i%4,20,orbit=speed)
for r in range(55,210,38): ring(450,280,r,"#fff",1,spin=.3)
title("MANDALA","математика симметрии")
bloom(1.6)`},
  {id:'cyber',title:'Кибер-сити',level:'medium',category:'visual',theme:'city',output:'canvas',tag:'random · loops',color:'#ff5f9e',brief:'Сгенерируй город: здания случайной высоты, сетка и неоновое солнце.',hints:['Высота — random.randint','Рисуй building() в цикле','Добавь grid() и sun()'],code:`from wow import *
import random
random.seed(12)
background("#08020e","#27113d")
color="#8b5cf6"; count=70; size=1.0; speed=1.0
grid(color,horizon=310)
step=35
for x in range(20,900,step):
    h=random.randint(35,int(160+size*50))
    building(x,465-h,step-6,h,random.choice(["#43c9ff","#ff5f9e","#ffc857"]))
for i in range(count): particle(random.randint(0,900),random.randint(0,470),"#ff5f9e",2,10,vy=speed)
sun(710,150,82,"#ff5f6d")
title("CYBER // 2088","процедурный город")`},
  {id:'aurora',title:'Северное сияние',level:'medium',category:'visual',theme:'nature',output:'canvas',tag:'waves · glow',color:'#45e6b5',brief:'Создай пять светящихся лент северного сияния.',hints:['Лента — список точек','Смести каждую ленту по Y','Измени фазу синуса'],code:`from wow import *
import math
background("#020b18","#0b2740")
color="#45e6b5"; count=150; size=8; speed=.8
stars(count,seed=7)
for band in range(5):
    points=[]
    for x in range(0,901,12):
        points.append((x,205+band*24+math.sin(x*.018+band)*42))
    c=palette(band,[color,"#43c9ff","#9b6cff"])
    wave(points,c,size,speed+band*.1)
orb(720,120,46,"#dffcff",28)
title("AURORA","сияние из циклов")`},
  {id:'galaxy',title:'Спиральная галактика',level:'hard',category:'visual',theme:'space',output:'canvas',tag:'math · random',color:'#9b6cff',brief:'Рассчитай трёхрукавную галактику минимум из 300 звёзд.',hints:['Рукав: i % 3','Угол смещай для каждого рукава','Сожми координату Y'],code:`from wow import *
import math,random
background("#05010f","#121a4d")
color="#8be9ff"; n=420; scale=1.0; speed=.25
for i in range(n):
    arm=i%3; r=(12+i*.72)*scale
    a=i*.14+arm*2.094
    x=450+math.cos(a)*r; y=280+math.sin(a)*r*.42
    c=random.choice(["#fff",color,"#b96cff"])
    particle(x,y,c,random.randint(1,4),18,orbit=speed)
orb(450,280,52,"#ff5f9e",34)
ring(450,280,105,"#8be9ff",3,spin=.7)
title("GALAXY 404","космос в 14 строках")`},
  {id:'vortex',title:'Портал NumPy',level:'hard',category:'visual',theme:'science',output:'canvas',tag:'numpy · geometry',color:'#43e6ff',brief:'Используй массив NumPy для построения светового портала.',hints:['Создай углы через np.linspace','Радиус увеличивается с i','Цвет чередуй через palette'],code:`from wow import *
import numpy as np
background("#020611","#131049")
color="#43e6ff"; n=360; scale=1.0; speed=.4
angles=np.linspace(0,10*np.pi,n)
for i,a in enumerate(angles):
    r=(35+i*.72)*scale
    x=450+np.cos(a)*r; y=280+np.sin(a)*r*.48
    c=palette(i,[color,"#8b5cf6","#ff5f9e"])
    particle(x,y,c,2+i%4,20,orbit=speed)
for r in range(60,220,32): ring(450,280,r,"#7cf8ff",2,spin=r/180)
title("NEON GATE","numpy создаёт портал")
bloom(1.8)`},
  {id:'password',title:'Генератор паролей',level:'easy',category:'useful',theme:'security',output:'console',tag:'secrets · strings',color:'#45e6b5',brief:'Создай криптостойкий пароль из букв, цифр и символов.',hints:['Подключи secrets и string','Выбирай символ через secrets.choice','Склей генератором строк'],code:`import secrets,string
length=16
symbols="!@#$%"
alphabet=string.ascii_letters+string.digits+symbols
password="".join(secrets.choice(alphabet) for _ in range(length))
print("🔐 Надёжный пароль:")
print(password)
print("\\nСимволов:",len(password))
print("Вариантов:",f"{len(alphabet)**length:,}")`},
  {id:'cipher',title:'Секретный шифр',level:'medium',category:'useful',theme:'security',output:'console',tag:'algorithm · strings',color:'#ffc857',brief:'Зашифруй сообщение сдвигом каждой буквы на семь позиций.',hints:['Найди индекс буквы','Добавь shift','Не забудь остаток % 26'],code:`import string
message="PYTHON IS POWER"; shift=7
abc=string.ascii_uppercase
result=""
for char in message:
    if char in abc:
        result+=abc[(abc.index(char)+shift)%26]
    else:
        result+=char
print("Исходник:",message)
print("Шифр:",result)`},
  {id:'quiz',title:'Игра-викторина',level:'easy',category:'games',theme:'quiz',output:'console',tag:'dict · score',color:'#43c9ff',brief:'Проверь три ответа и посчитай итоговый счёт.',hints:['Вопросы храни в словаре','Используй zip','True можно прибавлять к числу'],code:`questions={"Столица Японии?":"токио","7 * 8?":"56","Язык программы?":"python"}
answers=["Токио","56","Python"]
score=0; bonus=0
for (question,right),answer in zip(questions.items(),answers):
    ok=answer.lower()==right
    score+=ok
    print("✅" if ok else "❌",question,"—",answer)
print(f"\\nРезультат: {score+bonus}/{len(questions)}")`},
  {id:'dungeon',title:'Генератор лабиринта',level:'medium',category:'games',theme:'procedural',output:'console',tag:'random · ASCII',color:'#9b6cff',brief:'Сгенерируй новый ASCII-лабиринт с началом S и выходом X.',hints:['Создай матрицу из #','Внутри случайно меняй клетки на пробел','Соедини строки через join'],code:`import random
random.seed(21); w,h=31,13
grid=[["#" for _ in range(w)] for _ in range(h)]
for y in range(1,h-1):
    for x in range(1,w-1):
        if x%2 or y%2 or random.random()>.72:
            grid[y][x]=" "
grid[1][1]="S"; grid[h-2][w-2]="X"
print("ПРОЦЕДУРНЫЙ ЛАБИРИНТ\\n")
print("\\n".join("".join(row) for row in grid))`},
  {id:'recommend',title:'Мини-рекомендатель',level:'medium',category:'data',theme:'ai',output:'console',tag:'scores · AI',color:'#ff5f9e',brief:'Оцени фильмы по трём признакам и выведи рейтинг.',hints:['Признаки храни списком','Используй веса','Сортируй по итоговому score'],code:`movies={"Интерстеллар":[9,8,10],"Матрица":[10,9,7],"ВАЛЛ-И":[7,10,9],"Человек-паук":[8,7,8]}
weights=[.5,.2,.3]
scores={}
star_divisor=2
for name,features in movies.items():
    scores[name]=sum(a*b for a,b in zip(features,weights))
for name,score in sorted(scores.items(),key=lambda x:-x[1]):
    print("★"*round(score/star_divisor),name,round(score,1))`},
  {id:'stats',title:'Аналитика NumPy',level:'hard',category:'data',theme:'analytics',output:'console',tag:'numpy · statistics',color:'#45e6b5',brief:'Проанализируй оценки и определи направление тренда.',hints:['Создай np.array','Используй mean, median и std','Тренд можно найти через polyfit'],code:`import numpy as np
scores=np.array([72,88,91,64,95,83,77,100,86,90])
digits=1
print("📊 АНАЛИЗ РЕЗУЛЬТАТОВ")
print("Среднее:",round(scores.mean(),digits))
print("Медиана:",np.median(scores))
print("Разброс:",round(scores.std(),digits))
print("Лучший:",scores.max())
print("Выше среднего:",scores[scores>scores.mean()].tolist())
trend=np.polyfit(np.arange(len(scores)),scores,1)[0]
print("Тренд:","растёт ↗" if trend>0 else "падает ↘")`},
  {id:'graph',title:'Маршрут NetworkX',level:'hard',category:'data',theme:'graphs',output:'console',tag:'networkx · graphs',color:'#43c9ff',brief:'Найди самый короткий маршрут по взвешенному графу города.',hints:['Создай nx.Graph','Добавь weighted_edges','Используй shortest_path'],code:`import networkx as nx
city=nx.Graph()
city.add_weighted_edges_from([("Дом","Парк",4),("Дом","Метро",2),("Метро","Школа",5),("Парк","Кафе",3),("Кафе","Школа",2),("Метро","Кафе",7)])
start="Дом"; finish="Школа"
path=nx.shortest_path(city,start,finish,weight="weight")
distance=nx.shortest_path_length(city,start,finish,weight="weight")
print("🚀 Лучший маршрут:")
print(" → ".join(path))
print("Длина:",distance)`},
  {id:'morse',title:'Переводчик Морзе',level:'easy',category:'useful',theme:'communication',output:'console',tag:'dict · text',color:'#ffc857',brief:'Преврати короткое сообщение в настоящий код Морзе.',hints:['Создай словарь букв','Пройди по строке циклом','Разделяй сигналы пробелом'],code:`morse={"S":"...","O":"---","P":".--.","Y":"-.--","T":"-","H":"....","N":"-."}
message="PYTHON"
separator=" "
encoded=[]
for letter in message:
    encoded.append(morse[letter])
print("📡 Сообщение:",message)
print("Код Морзе:",separator.join(encoded))
print("Сигналов:",len(encoded))`},
  {id:'dna',title:'ДНК-детектор',level:'medium',category:'data',theme:'bioinformatics',output:'console',tag:'biology · strings',color:'#45e6b5',brief:'Исследуй цепочку ДНК: состав, GC-процент и обратный комплемент.',hints:['Посчитай A, T, G, C','GC% — доля G и C','Используй str.translate'],code:`from collections import Counter
dna="ATGCGATACGCTTACGGA"
digits=1
count=Counter(dna)
gc=(count["G"]+count["C"])/len(dna)*100
table=str.maketrans("ATGC","TACG")
reverse=dna.translate(table)[::-1]
print("🧬 Цепочка:",dna)
print("Состав:",dict(count))
print("GC-процент:",round(gc,digits))
print("Комплемент:",reverse)`},
  {id:'budget',title:'Умный бюджет',level:'hard',category:'useful',theme:'finance',output:'console',tag:'pandas · analytics',color:'#ff5f9e',brief:'Собери расходы по категориям и найди, где можно сэкономить.',hints:['Создай DataFrame','Сгруппируй сумму по category','Отсортируй результат'],code:`import pandas as pd
data={"category":["Еда","Транспорт","Еда","Игры","Транспорт","Книги"],"amount":[540,180,760,1200,220,650]}
df=pd.DataFrame(data)
currency="₽"; step=100
report=df.groupby("category")["amount"].sum().sort_values(ascending=False)
total=report.sum()
print("💳 РАСХОДЫ ЗА НЕДЕЛЮ")
for name,value in report.items():
    bar="█"*round(value/step)
    print(f"{name:10} {bar} {value} {currency}")
print("\nВсего:",total,currency)
print("Главная статья:",report.index[0])`}
];

export const LINE_GUIDES = {
  fireworks:[
    'Строка 1 — подключи все команды графического модуля wow.',
    'Строка 2 — подключи math для числа π, синуса и косинуса.',
    'Строка 3 — задай два цвета фона через background().',
    'Строка 4 — создай color, count, size, speed и glow своими значениями.',
    'Строка 5 — начни цикл for по количеству частиц.',
    'Строка 6 — внутри цикла рассчитай угол частицы через math.tau.',
    'Строка 7 — рассчитай скорость и вызови particle() с vx и vy.',
    'Строка 8 — после цикла добавь orb(), title() или bloom().'
  ],
  ocean:[
    'Строка 1 — подключи команды wow.',
    'Строка 2 — подключи math для синусоиды.',
    'Строка 3 — нарисуй фон через background().',
    'Строка 4 — создай color, size и speed.',
    'Строка 5 — начни внешний цикл для шести волн.',
    'Строка 6 — создай пустой список points.',
    'Строка 7 — во вложенном цикле рассчитай x и y через math.sin().',
    'Строка 8 — добавь точки в список и передай его в wave().'
  ],
  snow:[
    'Строка 1 — подключи wow.',
    'Строка 2 — подключи random.',
    'Строка 3 — задай random.seed(), чтобы результат повторялся.',
    'Строка 4 — создай фон и переменные color, count, max_size, speed.',
    'Строка 5 — начни цикл по count снежинкам.',
    'Строка 6 — внутри цикла получи случайные x, y и size.',
    'Строка 7 — вызови particle() и передай скорость падения в vy.',
    'Строка 8 — добавь луну через orb() и подпись через title().'
  ],
  mandala:[
    'Строка 1 — подключи wow, затем math.',
    'Строка 2 — задай фон.',
    'Строка 3 — создай color, n, scale и speed.',
    'Строка 4 — начни цикл по n точкам.',
    'Строка 5 — рассчитай угол и волнистый радиус.',
    'Строка 6 — получи x и y через cos() и sin().',
    'Строка 7 — нарисуй particle() и чередуй цвета через palette().',
    'Строка 8 — после цикла добавь кольца, заголовок и bloom().'
  ],
  cyber:[
    'Строка 1 — подключи wow и random.',
    'Строка 2 — задай random.seed().',
    'Строка 3 — создай фон, color, count, size и speed.',
    'Строка 4 — вызови grid() для перспективной сетки.',
    'Строка 5 — начни цикл по координатам x будущих зданий.',
    'Строка 6 — получи случайную высоту h.',
    'Строка 7 — вызови building() со случайным неоновым цветом.',
    'Строка 8 — отдельным циклом добавь частицы, затем sun() и title().'
  ],
  aurora:[
    'Строка 1 — подключи wow и math.',
    'Строка 2 — задай ночной фон.',
    'Строка 3 — создай color, count, size и speed.',
    'Строка 4 — вызови stars(count).',
    'Строка 5 — начни цикл для пяти световых лент.',
    'Строка 6 — создай points и вложенный цикл по x.',
    'Строка 7 — добавляй точки с y, рассчитанным через math.sin().',
    'Строка 8 — выбери цвет через palette() и вызови wave().'
  ],
  galaxy:[
    'Строка 1 — подключи wow, math и random.',
    'Строка 2 — задай космический фон.',
    'Строка 3 — создай color, n, scale и speed.',
    'Строка 4 — начни цикл по n звёздам.',
    'Строка 5 — вычисли номер рукава, радиус и угол.',
    'Строка 6 — получи x и сжатый y через cos() и sin().',
    'Строка 7 — выбери цвет и нарисуй particle() с orbit.',
    'Строка 8 — добавь ядро orb(), кольцо ring() и title().'
  ],
  vortex:[
    'Строка 1 — подключи wow.',
    'Строка 2 — импортируй numpy как np.',
    'Строка 3 — задай фон и переменные color, n, scale, speed.',
    'Строка 4 — создай массив angles через np.linspace().',
    'Строка 5 — начни цикл enumerate(angles).',
    'Строка 6 — рассчитай растущий радиус, x и y.',
    'Строка 7 — выбери цвет и вызови particle().',
    'Строка 8 — после цикла добавь кольца, title() и bloom().'
  ],
  password:[
    'Строка 1 — импортируй secrets и string.',
    'Строка 2 — задай длину пароля в length.',
    'Строка 3 — собери alphabet из букв, цифр и символов.',
    'Строка 4 — генератором выбери length случайных символов.',
    'Строка 5 — выведи заголовок.',
    'Строка 6 — выведи сам пароль.',
    'Строка 7 — выведи длину и количество возможных комбинаций.'
  ],
  cipher:[
    'Строка 1 — импортируй string.',
    'Строка 2 — создай message и shift.',
    'Строка 3 — сохрани алфавит и пустую строку result.',
    'Строка 4 — начни цикл по символам message.',
    'Строка 5 — проверь, входит ли символ в алфавит.',
    'Строка 6 — найди новый индекс через + shift и остаток % 26.',
    'Строка 7 — для пробела используй ветку else.',
    'Строка 8 — выведи исходное и зашифрованное сообщения.'
  ],
  quiz:[
    'Строка 1 — создай словарь вопросов и правильных ответов.',
    'Строка 2 — создай список ответов ученика.',
    'Строка 3 — обнули score.',
    'Строка 4 — начни цикл по парам из questions и answers.',
    'Строка 5 — сравни ответ без учёта регистра.',
    'Строка 6 — прибавь результат сравнения к score.',
    'Строка 7 — выведи ✅ или ❌ для каждого вопроса.',
    'Строка 8 — после цикла выведи общий результат.'
  ],
  dungeon:[
    'Строка 1 — импортируй random.',
    'Строка 2 — задай seed, ширину w и высоту h.',
    'Строка 3 — создай матрицу grid из символов #.',
    'Строка 4 — двумя циклами пройди по внутренним клеткам.',
    'Строка 5 — по условию меняй часть стен на пробелы.',
    'Строка 6 — поставь S в начало и X в выход.',
    'Строка 7 — склей клетки каждой строки через join().',
    'Строка 8 — выведи готовый лабиринт.'
  ],
  recommend:[
    'Строка 1 — создай словарь фильмов и трёх признаков.',
    'Строка 2 — создай список weights.',
    'Строка 3 — подготовь пустой словарь scores.',
    'Строка 4 — начни цикл по фильмам.',
    'Строка 5 — умножь признаки на веса через zip() и sum().',
    'Строка 6 — отсортируй scores по убыванию.',
    'Строка 7 — в цикле выведи звёзды, название и оценку.'
  ],
  stats:[
    'Строка 1 — импортируй numpy как np.',
    'Строка 2 — создай массив scores.',
    'Строка 3 — выведи заголовок анализа.',
    'Строка 4 — выведи mean(), median() и std().',
    'Строка 5 — найди максимум и значения выше среднего.',
    'Строка 6 — вычисли наклон trend через np.polyfit().',
    'Строка 7 — условным выражением выведи направление тренда.'
  ],
  graph:[
    'Строка 1 — импортируй networkx как nx.',
    'Строка 2 — создай пустой граф city.',
    'Строка 3 — добавь дороги с весами через add_weighted_edges_from().',
    'Строка 4 — найди path функцией shortest_path().',
    'Строка 5 — найди distance функцией shortest_path_length().',
    'Строка 6 — выведи заголовок.',
    'Строка 7 — склей точки маршрута стрелкой и выведи длину.'
  ],
  morse:[
    'Строка 1 — создай словарь букв и сигналов Морзе.',
    'Строка 2 — запиши слово в message.',
    'Строка 3 — подготовь пустой список encoded.',
    'Строка 4 — начни цикл по буквам message.',
    'Строка 5 — добавляй код буквы из словаря в encoded.',
    'Строка 6 — склей сигналы пробелами.',
    'Строка 7 — выведи исходное слово, код и число сигналов.'
  ],
  dna:[
    'Строка 1 — импортируй Counter.',
    'Строка 2 — запиши цепочку в dna.',
    'Строка 3 — посчитай буквы через Counter(dna).',
    'Строка 4 — рассчитай процент G и C.',
    'Строка 5 — создай таблицу замены A↔T и G↔C.',
    'Строка 6 — получи обратный комплемент.',
    'Строка 7 — выведи цепочку, состав, GC-процент и комплемент.'
  ],
  budget:[
    'Строка 1 — импортируй pandas как pd.',
    'Строка 2 — создай словарь data с категориями и суммами.',
    'Строка 3 — преврати data в DataFrame.',
    'Строка 4 — сгруппируй суммы и отсортируй по убыванию.',
    'Строка 5 — посчитай total и выведи заголовок.',
    'Строка 6 — начни цикл по строкам отчёта.',
    'Строка 7 — создай текстовую полосу bar и выведи категорию.',
    'Строка 8 — после цикла выведи общую сумму и главную статью.'
  ]
};

const variant=(key,label,options)=>({key,label,options});
export const PRACTICE_VARIANTS = {
  fireworks:[variant('color','Цвет частиц',['color="#ff5f9e"','color="#43e6ff"','color="gold"']),variant('size','Размер',['size=2','size=4','size=7']),variant('speed','Скорость',['speed=.6','speed=1.0','speed=1.8']),variant('count','Количество',['count=60','count=120','count=240'])],
  ocean:[variant('color','Цвет волн',['color="#43c9ff"','color="#45e6b5"','color="violet"']),variant('size','Толщина',['size=3','size=6','size=10']),variant('speed','Движение',['speed=.3','speed=1.0','speed=2.0'])],
  snow:[variant('color','Цвет снега',['color="white"','color="#dffcff"','color="#b8c8ff"']),variant('count','Снежинок',['count=80','count=180','count=350']),variant('size','Максимальный размер',['max_size=3','max_size=5','max_size=9'])],
  mandala:[variant('color','Главный цвет',['color="gold"','color="#ff5f9e"','color="#43e6ff"']),variant('count','Точек',['n=48','n=96','n=180']),variant('speed','Вращение',['speed=.2','speed=.5','speed=1.2'])],
  cyber:[variant('color','Цвет сетки',['color="#8b5cf6"','color="#43e6ff"','color="#ff5f9e"']),variant('count','Частиц',['count=30','count=70','count=150']),variant('size','Высота зданий',['size=.6','size=1.0','size=1.8'])],
  aurora:[variant('color','Цвет сияния',['color="#45e6b5"','color="#43c9ff"','color="#b96cff"']),variant('count','Звёзд',['count=60','count=150','count=300']),variant('size','Толщина лент',['size=4','size=8','size=14'])],
  galaxy:[variant('color','Цвет звёзд',['color="#8be9ff"','color="#ffcc70"','color="#ff8bd4"']),variant('count','Звёзд',['n=180','n=420','n=700']),variant('speed','Вращение',['speed=.1','speed=.25','speed=.7'])],
  vortex:[variant('color','Цвет портала',['color="#43e6ff"','color="#9b6cff"','color="#ff5f9e"']),variant('count','Частиц',['n=160','n=360','n=650']),variant('speed','Вращение',['speed=.15','speed=.4','speed=1.0'])],
  password:[variant('length','Длина пароля',['length=8','length=12','length=18']),variant('symbols','Набор символов',['symbols="!@#"','symbols="!@#$%"','symbols="!@#$%^&*"'])],
  cipher:[variant('message','Сообщение',['message="HELLO"','message="PYTHON IS POWER"','message="SECRET MISSION"']),variant('shift','Сдвиг',['shift=3','shift=7','shift=13'])],
  quiz:[variant('answers','Ответы игрока',['answers=["Токио","56","Python"]','answers=["Киото","54","Python"]','answers=["Токио","56","Java"]']),variant('bonus','Бонусные очки',['bonus=0','bonus=1','bonus=2'])],
  dungeon:[variant('width','Ширина',['w=21','w=31','w=45']),variant('height','Высота',['h=9','h=13','h=19']),variant('seed','Вариант карты',['random.seed(7)','random.seed(21)','random.seed(99)'])],
  recommend:[variant('focus','Главный признак',['weights=[.6,.2,.2]','weights=[.3,.5,.2]','weights=[.3,.2,.5]']),variant('stars','Шкала звёзд',['star_divisor=1','star_divisor=2','star_divisor=3'])],
  stats:[variant('scores','Набор оценок',['scores=[72,88,91,64,95]','scores=[5,7,8,9,10]','scores=[100,82,76,91,88]']),variant('rounding','Точность',['digits=0','digits=1','digits=2'])],
  graph:[variant('start','Начальная точка',['start="Дом"','start="Парк"','start="Метро"']),variant('finish','Конечная точка',['finish="Школа"','finish="Кафе"','finish="Парк"'])],
  morse:[variant('message','Сообщение',['message="SOS"','message="PYTHON"','message="TOP SECRET"']),variant('separator','Разделитель',['separator=" "','separator=" / "','separator=" • "'])],
  dna:[variant('dna','Цепочка',['dna="ATGCGATACG"','dna="GGCCTTAAGC"','dna="ATATCGCGAT"']),variant('precision','Точность процента',['digits=0','digits=1','digits=2'])],
  budget:[variant('currency','Валюта',['currency="₽"','currency="$"','currency="€"']),variant('bar','Масштаб полосы',['step=50','step=100','step=200'])]
};

export function validatePythonSource(source=''){const meaningful=source.split(/\r?\n/).filter(line=>line.trim()&&!line.trim().startsWith('#'));if(meaningful.length>MAX_PYTHON_LINES)return{ok:false,lines:meaningful.length,message:`Для вау-проекта используй не больше ${MAX_PYTHON_LINES} строк кода`};return{ok:true,lines:meaningful.length,message:''}}
export function formatPythonError(raw=''){const matches=[...String(raw).matchAll(/line\s+(\d+)/gi)];const line=matches.length?Number(matches.at(-1)[1]):null;const final=String(raw).trim().split(/\r?\n/).filter(Boolean).at(-1)||'Неизвестная ошибка Python';return{line,text:final.replace(/^\w*Error:\s*/,'')}}
const allowed=new Set(['background','particle','orb','ring','title','bloom','wave','stars','grid','building','sun','line','polygon']);
export function sanitizeFrame(commands){if(!Array.isArray(commands))return[];return commands.filter(item=>item&&allowed.has(item.type)).slice(0,2000).map(item=>{const clean={...item};for(const key of['x','y','r','size','glow','width','height','spin','orbit','vx','vy','drift','horizon','strength'])if(key in clean)clean[key]=Number.isFinite(Number(clean[key]))?Number(clean[key]):0;return clean})}

