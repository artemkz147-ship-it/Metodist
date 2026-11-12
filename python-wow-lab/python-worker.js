const PYODIDE_BASE = 'https://cdn.jsdelivr.net/pyodide/v0.29.5/full/';
let pyodidePromise;

const WOW_MODULE = `
import sys, types, math, random
wow = types.ModuleType("wow")
wow._commands = []

def _num(value):
    try: return float(value)
    except: return 0.0
def _cmd(kind, **data):
    data["type"] = kind
    wow._commands.append(data)
def background(a="#050714", b=None): _cmd("background", a=str(a), b=str(b or a))
def particle(x,y,color="#ffffff",size=3,glow=12,vx=0,vy=0,orbit=0): _cmd("particle",x=_num(x),y=_num(y),color=str(color),size=_num(size),glow=_num(glow),vx=_num(vx),vy=_num(vy),orbit=_num(orbit),seed=random.random()*99)
def orb(x,y,r,color="#8b5cf6",glow=25): _cmd("orb",x=_num(x),y=_num(y),r=_num(r),color=str(color),glow=_num(glow))
def ring(x,y,r,color="#43c9ff",width=2,spin=0): _cmd("ring",x=_num(x),y=_num(y),r=_num(r),color=str(color),width=_num(width),spin=_num(spin))
def title(big,small=""): _cmd("title",big=str(big),small=str(small))
def bloom(strength=1): _cmd("bloom",strength=_num(strength))
def wave(points,color="#45e6b5",width=6,drift=0): _cmd("wave",points=[[_num(x),_num(y)] for x,y in points],color=str(color),width=_num(width),drift=_num(drift))
def stars(count=100,seed=1):
    rng=random.Random(seed)
    for _ in range(min(int(count),500)): particle(rng.randint(0,900),rng.randint(0,560),"#ffffff",rng.choice([1,1,2]),rng.randint(3,14))
def grid(color="#8b5cf6",horizon=300): _cmd("grid",color=str(color),horizon=_num(horizon))
def building(x,y,width,height,color="#43c9ff"): _cmd("building",x=_num(x),y=_num(y),width=_num(width),height=_num(height),color=str(color),seed=random.random()*99)
def sun(x,y,r,color="#ff5f6d"): _cmd("sun",x=_num(x),y=_num(y),r=_num(r),color=str(color))
def line(x1,y1,x2,y2,color="#ffffff",width=2): _cmd("line",x=_num(x1),y=_num(y1),x2=_num(x2),y2=_num(y2),color=str(color),width=_num(width))
def polygon(points,color="#8b5cf6",glow=10): _cmd("polygon",points=[[_num(x),_num(y)] for x,y in points],color=str(color),glow=_num(glow))
def palette(index,colors): return str(colors[int(index)%len(colors)])

for name, value in list(locals().items()):
    if not name.startswith("_") and name not in ("sys","types","math","random","wow"):
        setattr(wow, name, value)
sys.modules["wow"] = wow
`;

async function getPyodide() {
  if (!pyodidePromise) pyodidePromise = (async () => {
    postMessage({ type: 'status', stage: 'engine', text: 'Загружаю настоящий Python 3…' });
    importScripts(PYODIDE_BASE + 'pyodide.js');
    const runtime = await loadPyodide({ indexURL: PYODIDE_BASE });
    await runtime.runPythonAsync(WOW_MODULE);
    postMessage({ type: 'ready' });
    return runtime;
  })();
  return pyodidePromise;
}

self.onmessage = async event => {
  if (event.data?.type !== 'run') return;
  try {
    const pyodide = await getPyodide();
    const code = String(event.data.code || '');
    const packages = [];
    for (const name of ['numpy','pandas','matplotlib','scipy','sympy','networkx']) {
      if (new RegExp(`(^|\\n)\\s*(from|import)\\s+${name}\\b`).test(code)) packages.push(name);
    }
    if (packages.length) {
      postMessage({ type: 'status', stage: 'packages', text: `Подключаю: ${packages.join(', ')}…` });
      await pyodide.loadPackage(packages);
    }
    pyodide.runPython('import wow; wow._commands.clear()');
    pyodide.setStdout({ batched: text => postMessage({ type: 'stdout', text }) });
    await pyodide.runPythonAsync(code);
    const json = pyodide.runPython('import wow, json; json.dumps(wow._commands)');
    postMessage({ type: 'result', commands: JSON.parse(json), packages });
  } catch (error) {
    postMessage({ type: 'error', error: String(error?.message || error) });
  }
};

