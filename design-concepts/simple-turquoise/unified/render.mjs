/** Render portable design HTML with installed Chrome; no APK or dependencies. */
import {spawn} from 'node:child_process';
import {readFile,writeFile,mkdtemp,rm} from 'node:fs/promises';
import {tmpdir} from 'node:os';
import {join,dirname} from 'node:path';
import {fileURLToPath,pathToFileURL} from 'node:url';
const here=dirname(fileURLToPath(import.meta.url));
const profile=await mkdtemp(join(tmpdir(),'qida-design-'));
const chrome=spawn('/Applications/Google Chrome.app/Contents/MacOS/Google Chrome',['--headless','--disable-gpu','--no-sandbox','--remote-debugging-port=0',`--user-data-dir=${profile}`,'--allow-file-access-from-files','--hide-scrollbars'],{stdio:'ignore'});
const delay=ms=>new Promise(r=>setTimeout(r,ms));
let ws;
try {
  let port;
  for(let i=0;i<100;i++){try{port=(await readFile(join(profile,'DevToolsActivePort'),'utf8')).split('\n')[0];break;}catch{await delay(100);}}
  if(!port)throw Error('Chrome did not start');
  const target=await (await fetch(`http://127.0.0.1:${port}/json/new?about:blank`,{method:'PUT'})).json();
  ws=new WebSocket(target.webSocketDebuggerUrl);
  await new Promise((r,j)=>{ws.onopen=r;ws.onerror=j;});
  let id=0;const pending=new Map();
  ws.onmessage=e=>{const d=JSON.parse(e.data);if(d.id){const p=pending.get(d.id);pending.delete(d.id);d.error?p.reject(Error(JSON.stringify(d.error))):p.resolve(d.result);}};
  const call=(method,params={})=>new Promise((resolve,reject)=>{const n=++id;pending.set(n,{resolve,reject});ws.send(JSON.stringify({id:n,method,params}));});
  await call('Page.enable');
  const sheets=JSON.parse(await readFile(join(here,'screens.json'),'utf8'));
  const report=[];
  for(const s of sheets){
    const width=64+s.panels.length*400+(s.panels.length-1)*24;
    await call('Emulation.setDeviceMetricsOverride',{width,height:1200,deviceScaleFactor:1,mobile:false});
    await call('Page.navigate',{url:pathToFileURL(join(here,s.file+'.html')).href});
    let ready=false;
    for(let i=0;i<100;i++){
      const r=await call('Runtime.evaluate',{expression:'document.readyState === "complete" && [...document.images].every(i=>i.complete)',returnByValue:true});
      if(r.result.value){ready=true;break;} await delay(50);
    }
    if(!ready)throw Error('Images not ready '+s.file);
    const qa=(await call('Runtime.evaluate',{expression:`JSON.stringify({height:document.documentElement.scrollHeight,images:[...document.images].filter(i=>!i.naturalWidth).map(i=>i.src),overflow:[...document.querySelectorAll('main,.card,.field,.button,nav')].filter(e=>e.scrollWidth>e.clientWidth+1).map(e=>({text:e.textContent.slice(0,100),width:e.clientWidth,scroll:e.scrollWidth}))})`,returnByValue:true})).result.value;
    const q=JSON.parse(qa);
    await call('Emulation.setDeviceMetricsOverride',{width,height:q.height,deviceScaleFactor:1,mobile:false});
    await delay(50);
    const shot=await call('Page.captureScreenshot',{format:'png',captureBeyondViewport:true,clip:{x:0,y:0,width,height:q.height,scale:1}});
    await writeFile(join(here,s.file+'.png'),Buffer.from(shot.data,'base64'));
    report.push({sheet:s.file,width,...q});
    console.log(s.file,`${width}×${q.height}`,`overflow=${q.overflow.length}`);
  }
  await writeFile(join(here,'render-report.json'),JSON.stringify(report,null,2)+'\n');
}finally{ws?.close();chrome.kill();await delay(300);await rm(profile,{recursive:true,force:true,maxRetries:5,retryDelay:100});}
