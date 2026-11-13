export const MAX_PYTHON_LINES = 15;

export const WOW_EXAMPLES = [
  {id:'sailing',title:'Парусная регата',level:'easy',category:'games',theme:'ocean',output:'canvas',interactive:true,tag:'ветер · волны · управление',color:'#43e6ff',brief:'Проведи яхту через буи, поймай ветер и финишируй быстрее таймера.',code:`from wow import *
wind=1.2
waves=18
color="#ffd166"
turn=2.4
buoys=5
sailing(wind=wind,waves=waves,boat=color,turn=turn,buoys=buoys)`},
  {id:'blocks',title:'Неоновые блоки',level:'easy',category:'games',theme:'blocks',output:'canvas',interactive:true,tag:'фигуры · линии · очки',color:'#9b6cff',brief:'Собирай полные линии, ускоряй падение и не дай башне дойти до верха.',code:`from wow import *
width=10
height=18
speed=1.0
colors=["#43e6ff","#ff5f9e","#ffd166"]
shapes="IJLOSTZ"
line_score=100
blocks(width=width,height=height,speed=speed,colors=colors,shapes=shapes,line_score=line_score)`},
  {id:'starfighter',title:'Звёздный защитник',level:'medium',category:'games',theme:'space',output:'canvas',interactive:true,tag:'враги · выстрелы · волны',color:'#ff5f9e',brief:'Уничтожь строй пришельцев, уклоняйся от огня и сохрани жизни.',code:`from wow import *
speed=5
enemies=18
bullet_speed=9
lives=3
color="#43e6ff"
difficulty=1.2
starfighter(speed=speed,enemies=enemies,bullet_speed=bullet_speed,lives=lives,color=color,difficulty=difficulty)`},
  {id:'neon_racer',title:'Неоновая гонка',level:'medium',category:'games',theme:'racing',output:'canvas',interactive:true,tag:'скорость · соперники · круги',color:'#45e6b5',brief:'Обгоняй соперников на световой трассе и успей закончить три круга.',code:`from wow import *
speed=7
grip=0.82
rivals=5
obstacles=8
color="#45e6b5"
laps=3
neon_racer(speed=speed,grip=grip,rivals=rivals,obstacles=obstacles,color=color,laps=laps)`},
  {id:'dungeon_escape',title:'Побег из подземелья',level:'hard',category:'games',theme:'dungeon',output:'canvas',interactive:true,tag:'лабиринт · ключи · враги',color:'#ffc857',brief:'Исследуй тёмный лабиринт, собери ключи и найди выход раньше врагов.',code:`from wow import *
size=15
keys=3
enemies=4
speed=4
colors=["#ffc857","#8b5cf6","#45e6b5"]
lives=3
dungeon_escape(size=size,keys=keys,enemies=enemies,speed=speed,colors=colors,lives=lives)`},
  {id:'physics_lab',title:'Физическая песочница',level:'hard',category:'games',theme:'physics',output:'canvas',interactive:true,tag:'гравитация · импульс · взрывы',color:'#ff7a59',brief:'Меняй законы мира, запускай импульсы и устрой каскад светящихся столкновений.',code:`from wow import *
gravity=7.5
bodies=24
bounce=0.86
blast=16
colors=["#43e6ff","#ff5f9e","#ffd166"]
target=40
physics_lab(gravity=gravity,bodies=bodies,bounce=bounce,blast=blast,colors=colors,target=target)`}
];

export const LINE_GUIDES = Object.fromEntries(WOW_EXAMPLES.map(mission => [mission.id,
  mission.code.split('\n').filter(Boolean).map((line,index)=>`Строка ${index+1} — ${index===0?'подключи игровые команды wow.':line.includes('(')&&index===mission.code.split('\n').length-1?'запусти мир с выбранными параметрами.':'создай параметр и выбери своё значение.'}`)
]));

export const PRACTICE_VARIANTS = {
  sailing:[
    {key:'wind',label:'Сила ветра',options:['wind=.6','wind=1.2','wind=2.0']},
    {key:'waves',label:'Высота волн',options:['waves=8','waves=18','waves=30']},
    {key:'boat',label:'Цвет паруса',options:['color="#ffd166"','color="#43e6ff"','color="#ff5f9e"']},
    {key:'turn',label:'Манёвренность',options:['turn=1.6','turn=2.4','turn=3.4']}
  ],
  blocks:[
    {key:'speed',label:'Скорость падения',options:['speed=.65','speed=1.0','speed=1.6']},
    {key:'width',label:'Ширина поля',options:['width=8','width=10','width=12']},
    {key:'shapes',label:'Набор фигур',options:['shapes="IJLOSTZ"','shapes="ILOT"','shapes="IJSZ"']},
    {key:'score',label:'Очки за линию',options:['line_score=50','line_score=100','line_score=250']}
  ],
  starfighter:[
    {key:'enemies',label:'Число врагов',options:['enemies=12','enemies=18','enemies=28']},
    {key:'speed',label:'Скорость корабля',options:['speed=3','speed=5','speed=7']},
    {key:'lives',label:'Жизни',options:['lives=1','lives=3','lives=5']},
    {key:'difficulty',label:'Сложность',options:['difficulty=.7','difficulty=1.2','difficulty=1.8']}
  ],
  neon_racer:[
    {key:'speed',label:'Максимальная скорость',options:['speed=5','speed=7','speed=10']},
    {key:'grip',label:'Сцепление',options:['grip=.65','grip=.82','grip=.95']},
    {key:'rivals',label:'Соперники',options:['rivals=3','rivals=5','rivals=8']},
    {key:'laps',label:'Круги',options:['laps=1','laps=3','laps=5']}
  ],
  dungeon_escape:[
    {key:'size',label:'Размер лабиринта',options:['size=11','size=15','size=19']},
    {key:'keys',label:'Ключи',options:['keys=1','keys=3','keys=5']},
    {key:'enemies',label:'Враги',options:['enemies=2','enemies=4','enemies=7']},
    {key:'lives',label:'Жизни',options:['lives=1','lives=3','lives=5']}
  ],
  physics_lab:[
    {key:'gravity',label:'Гравитация',options:['gravity=1.5','gravity=7.5','gravity=14']},
    {key:'bodies',label:'Количество тел',options:['bodies=12','bodies=24','bodies=42']},
    {key:'bounce',label:'Упругость',options:['bounce=.45','bounce=.86','bounce=1.0']},
    {key:'blast',label:'Сила импульса',options:['blast=8','blast=16','blast=28']}
  ]
};

export function validatePythonSource(source=''){const meaningful=source.split(/\r?\n/).filter(line=>line.trim()&&!line.trim().startsWith('#'));if(meaningful.length>MAX_PYTHON_LINES)return{ok:false,lines:meaningful.length,message:`Для вау-проекта используй не больше ${MAX_PYTHON_LINES} строк кода`};return{ok:true,lines:meaningful.length,message:''}}
export function formatPythonError(raw=''){const matches=[...String(raw).matchAll(/line\s+(\d+)/gi)];const line=matches.length?Number(matches.at(-1)[1]):null;const final=String(raw).trim().split(/\r?\n/).filter(Boolean).at(-1)||'Неизвестная ошибка Python';return{line,text:final.replace(/^\w*Error:\s*/,'')}}
const allowed=new Set(['background','particle','orb','ring','title','bloom','wave','stars','grid','building','sun','line','polygon']);
const gameTypes=new Set(['sailing','blocks','starfighter','neon_racer','dungeon_escape','physics_lab']);
const clamp=(value,min,max,fallback=min)=>Math.min(max,Math.max(min,Number.isFinite(Number(value))?Number(value):fallback));
const integer=(value,min,max,fallback=min)=>Math.round(clamp(value,min,max,fallback));
const text=(value,fallback)=>String(value??fallback).slice(0,40);
const palette=(value,fallback)=>Array.isArray(value)?value.slice(0,8).map(color=>text(color,'#ffffff')):fallback;
function sanitizeGame(item){
  if(item.type==='sailing')return{type:item.type,wind:clamp(item.wind,.2,3,1.2),waves:clamp(item.waves,4,40,18),boat:text(item.boat,'#ffd166'),turn:clamp(item.turn,.8,4,2.4),buoys:integer(item.buoys,3,9,5)};
  if(item.type==='blocks')return{type:item.type,width:integer(item.width,8,14,10),height:integer(item.height,8,24,18),speed:clamp(item.speed,.2,3,1),colors:palette(item.colors,['#43e6ff','#ff5f9e','#ffd166']),shapes:text(item.shapes,'IJLOSTZ').replace(/[^IJLOSTZ]/g,'')||'IJLOSTZ',line_score:integer(item.line_score,10,1000,100)};
  if(item.type==='starfighter')return{type:item.type,speed:clamp(item.speed,2,10,5),enemies:integer(item.enemies,6,36,18),bullet_speed:clamp(item.bullet_speed,3,14,9),lives:integer(item.lives,1,7,3),color:text(item.color,'#43e6ff'),difficulty:clamp(item.difficulty,.5,2.5,1.2)};
  if(item.type==='neon_racer')return{type:item.type,speed:clamp(item.speed,4,12,7),grip:clamp(item.grip,.4,1,.82),rivals:integer(item.rivals,2,10,5),obstacles:integer(item.obstacles,0,16,8),color:text(item.color,'#45e6b5'),laps:integer(item.laps,1,5,3)};
  if(item.type==='dungeon_escape'){let size=integer(item.size,11,21,15);if(size%2===0)size-=1;return{type:item.type,size,keys:integer(item.keys,1,7,3),enemies:integer(item.enemies,1,9,4),speed:clamp(item.speed,2,8,4),colors:palette(item.colors,['#ffc857','#8b5cf6','#45e6b5']),lives:integer(item.lives,1,7,3)}}
  return{type:item.type,gravity:clamp(item.gravity,0,20,7.5),bodies:integer(item.bodies,6,60,24),bounce:clamp(item.bounce,.2,1,.86),blast:clamp(item.blast,4,30,16),colors:palette(item.colors,['#43e6ff','#ff5f9e','#ffd166']),target:integer(item.target,10,200,40)};
}
export function sanitizeFrame(commands){if(!Array.isArray(commands))return[];return commands.filter(item=>item&&(allowed.has(item.type)||gameTypes.has(item.type))).slice(0,2000).map(item=>{if(gameTypes.has(item.type))return sanitizeGame(item);const clean={...item};for(const key of['x','y','r','size','glow','width','height','spin','orbit','vx','vy','drift','horizon','strength'])if(key in clean)clean[key]=Number.isFinite(Number(clean[key]))?Number(clean[key]):0;return clean})}

