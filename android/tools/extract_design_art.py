"""Historical artwork extraction only, not current UI references; macOS sips, no bitmap redesign.
For current UI use canonical transparent assets (design-concepts/README.md).
Run historical regeneration manually after inspecting source images. Not part of CI.
"""
import subprocess
from pathlib import Path
from design_archive import reference_file
root=Path(__file__).resolve().parents[2]
out=root/'android/app/src/main/res/drawable-nodpi'
specs={
'01-baslangic.png':{'art_welcome':(105,193,395,400),'art_log':(108,481,183,546),'art_menu':(108,556,213,616),'art_progress':(116,637,206,696),'art_sedentary':(925,248,1066,370),'art_walking':(929,399,1064,523),'art_active':(925,563,1063,679),'art_male':(540,552,619,630),'art_female':(715,548,802,631),'art_age':(509,246,560,298),'art_height':(511,326,559,384),'art_weight':(508,408,564,459)},
'02-secimler-ve-hesab.png':{'art_cooking':(580,225,841,338),'art_milk':(129,282,203,357),'art_egg':(232,298,313,355),'art_nuts':(341,289,425,357),'art_fish':(133,435,263,492),'art_wheat':(309,433,418,490)},
'00-reference.png':{'art_entry_camera':(519,369,600,449),'art_entry_search':(519,506,600,587),'art_entry_previous':(519,643,600,725),'art_lose':(965,1227,1035,1301),'art_maintain':(1131,1224,1203,1301),'art_gain':(965,1421,1033,1490),'art_healthy':(1127,1413,1210,1506)},
'12-elave-profil-secimleri.png':{'art_health':(137,134,377,299),'art_budget':(558,150,801,304),'art_saved':(1387,166,1621,342)},
'13-bos-veziyyetler.png':{'art_empty_menu':(91,215,409,508),'art_empty_add':(515,214,832,506),'art_empty_progress':(940,218,1260,513),'art_account':(1390,217,1656,474)},
'14-elave-formalar-ve-veziyyetler.png':{'art_other_choices':(130,136,380,302),'art_dislikes':(540,142,801,315),'art_write_error':(1380,200,1630,395)},
'06-abunelik-ve-veziyyetler.png':{'art_empty':(525,190,817,419)},
}
for file,items in specs.items():
 for name,(left,top,right,bottom) in items.items():
  if file == '00-reference.png':
   left,top,right,bottom = [round(v*2880/1333) for v in (left,top,right,bottom)]
  subprocess.run(['sips','-c',str(bottom-top),str(right-left),'--cropOffset',str(top),str(left),str(reference_file(root,'design-concepts/simple-turquoise/'+file)),'--out',str(out/(name+'.png'))],check=True,stdout=subprocess.DEVNULL)
