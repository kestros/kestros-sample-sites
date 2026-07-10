#!/usr/bin/env python3
"""ACPL league-data generator -> JSON for the 12 datasources.
Deterministic (fixed seed). Simulates 3 seasons: fixtures, results, standings,
player goal/assist/apps/cards attribution (season + career), match summaries, stories."""
import json, os, random
from datetime import date, timedelta
SEED=20260709
OUT=os.path.join(os.path.dirname(__file__),'out'); os.makedirs(OUT,exist_ok=True)
def W(name,obj): json.dump(obj, open(os.path.join(OUT,name),'w'), indent=2)

CLUBS=[
 ('harborside',"Harborside United","HU","Harborside",1912,"Harborside Coliseum",38400,'#0B1E3F','#D4AF37',"Petra Yilmaz"),
 ('albion',"Albion FC","AFC","Albion",1899,"Albion Park",24100,'#C8102E','#FFFFFF',"R. Tanaka"),
 ('cape-rangers',"Cape Rangers","CR","Cape Vincent",1908,"The Headland",21750,'#1B4332','#000000',"J. Marchetti"),
 ('saint-marys',"Saint Mary's FC","SM","Saint Mary's",1888,"The Abbey",19900,'#D7141A','#000000',"O. Kowalski"),
 ('riverside',"Riverside City","RC","Riverside",1937,"River Park",26300,'#14746F','#C0C0C0',"M. Donovan"),
 ('westmoreland',"Westmoreland Athletic","WA","Westmoreland",1925,"Royal Ground",28800,'#6B2C8A','#FFFFFF',"A. Vance"),
 ('bayview',"Bayview United","BU","Bayview",1956,"Bayview Arena",23400,'#00BCD4','#2C2C2C',"L. Ortega"),
 ('highland',"Highland Park","HP","Highland Park",1946,"Park Stadium",17600,'#6CB4EE','#FFFFFF',"I. Bekova"),
 ('bridgeport',"Bridgeport FC","BFC","Bridgeport",1903,"The Bridge",20500,'#FFCC00','#000000',"C. Maddox"),
 ('northgate',"Northgate Rovers","NR","Northgate",1901,"Rovers Stadium",22100,'#F4791F','#0B1E3F',"E. Petrov"),
 ('oakvale',"Oakvale Wanderers","OW","Oakvale",1919,"Oak Lane",16800,'#7B1E2B','#F5EBD7',"S. Fellner"),
 ('marshall',"Marshall Town","MT","Marshall",1962,"Founders Field",15200,'#E91E63','#455A64',"D. Aaronson"),
]
clubs=[dict(slug=s,name=n,short=sh,city=c,founded=f,stadium=st,capacity=cap,mgr=mg,primary=p,secondary=sec)
       for (s,n,sh,c,f,st,cap,p,sec,mg) in CLUBS]
clubname={c['slug']:c['name'] for c in clubs}
clubshort={c['slug']:c['short'] for c in clubs}

FIRST=["Mateo","Diego","Aiden","Bastian","Felipe","Sang-jin","Hugh","Yusuf","Oliver","Jordan","Tomas","Sven","Kasper","Lukas","Marco","Iván","Theo","Patrice","Niko","Eli","Caio","Stefan","Andre","Luca","Mohammed","Kenji","Dmitri","Owen","Rafael","Sacha","Emil","Noah","Jonas","Viktor","Leon","Adam","Hassan","Tariq","Miles","Cole","Dante","Rory","Finn","Kai","Aaron","Bruno","Nikolai","Samir","Idris","Pavel","Gabriel","Hakim","Anton","Ravi","Malik","Enzo","Piotr","Sean","Otto","Yannick"]
LAST=["Reyes","Lowe","Marsh","Tovar","Adekunle","Choi","Carter","Akin","Reid","Bex","Vela","Riley","Beck","Holm","Russo","Ramos","Bramble","Ndiaye","Petrov","Whitfield","Mendes","Korhonen","Silva","Bianchi","Haddad","Watanabe","Volkov","Pryce","Ferreira","Dubois","Berg","Novak","Lindqvist","Sorensen","Fischer","Kovac","Nasser","Rahman","Bennett","Sinclair","Moreau","Quinn","ONeill","Larsen","Walsh","Costa","Ivanov","ElAmin","Khan","Sokolov","Marchetti","Yilmaz","Osei","Bright","Falcone","Vidic","Serrano","Park","Ashworth","Delgado"]
NATS=["Uruguayan","Japanese","Korean","Nigerian","Italian","Danish","Brazilian","English","French","Russian","German","Senegalese","Spanish","Dutch","Argentine"]
# ~26-man squad
POS_LAYOUT=[('GK',3),('CB',5),('RB',2),('LB',2),('CDM',3),('CM',4),('CAM',2),('RW',2),('LW',2),('ST',3)]
SCORE_W={'ST':10,'CAM':7,'RW':6,'LW':6,'CM':4,'CDM':2,'RB':2,'LB':2,'CB':1.5,'GK':0.02}
ASSIST_W={'CAM':9,'RW':7,'LW':7,'CM':6,'ST':5,'RB':4,'LB':4,'CDM':3,'CB':1.5,'GK':0.05}

HARBORSIDE_SQUAD=[
 (1,'GK','Aiden Marsh'),(2,'RB','Diego Lowe'),(3,'CB','Bastian Tovar'),(4,'CB','Felipe Adekunle'),
 (5,'LB','Sang-jin Choi'),(6,'CDM','Hugh Carter'),(7,'RW','Yusuf Akin'),(8,'CM','Oliver Reid'),
 (9,'ST','Jordan Bex'),(10,'CAM','Mateo Reyes'),(11,'LW','Tomas Vela'),(12,'GK','Sven Riley'),
 (14,'CM','Kasper Beck'),(15,'CB','Lukas Holm'),(16,'RB','Marco Russo'),(17,'LB','Iván Ramos'),
 (18,'CM','Theo Bramble'),(19,'ST','Patrice Ndiaye'),(20,'CAM','Niko Petrov'),(21,'LW','Eli Whitfield'),
 (22,'RW','Caio Mendes'),(23,'ST','Stefan Korhonen'),(24,'CB','Andre Berg'),(25,'CDM','Rory Quinn'),
 (26,'ST','Kai Osei'),(27,'GK','Owen Pryce'),
]
NAT_HB={'Mateo Reyes':'Uruguayan','Jordan Bex':'English','Yusuf Akin':'Turkish'}
def harborside_squad():
    out=[]
    for num,pos,nm in HARBORSIDE_SQUAD:
        r=random.Random(SEED+hash(nm)%9999)
        out.append(dict(slug="harborside-%s"%nm.lower().replace(' ','-'), name=nm, club='harborside', pos=pos,
            num=num, age=r.randint(20,33), height="%.2f m"%r.uniform(1.72,1.94),
            nationality=NAT_HB.get(nm, r.choice(NATS))))
    return out

def gen_squad(club_slug, off):
    r=random.Random(SEED+off); used=set(); players=[]; num=1
    for pos,count in POS_LAYOUT:
        for _ in range(count):
            while True:
                nm="%s %s"%(r.choice(FIRST),r.choice(LAST))
                if nm not in used: used.add(nm); break
            players.append(dict(slug="%s-%s"%(club_slug,nm.lower().replace(' ','-')), name=nm, club=club_slug,
                pos=pos, num=num, age=r.randint(19,34), height="%.2f m"%r.uniform(1.70,1.96),
                nationality=r.choice(NATS)))
            num+=1
            if num==13: num=14
    return players
squads={c['slug']:(harborside_squad() if c['slug']=='harborside' else gen_squad(c['slug'], i*1000)) for i,c in enumerate(clubs)}
allplayers={p['slug']:p for sq in squads.values() for p in sq}

STR={'harborside':1.35,'albion':0.72,'cape-rangers':0.62,'saint-marys':0.55,'riverside':0.45,'westmoreland':0.38,
     'bayview':0.30,'highland':0.18,'bridgeport':0.05,'northgate':-0.1,'oakvale':-0.3,'marshall':-0.5}

def pois(r,lam):
    L=pow(2.718281828,-lam); k=0; p=1.0
    while True:
        k+=1; p*=r.random()
        if p<=L: return k-1

def weighted_pick(r, squad, wmap):
    weights=[wmap.get(p['pos'],1) for p in squad]
    return r.choices(squad, weights=weights, k=1)[0]

GOAL_NOTES=["","","","","penalty","header","free-kick","header from corner","volley","tap-in"]

def schedule(slugs):
    n=len(slugs); rot=slugs[1:]; fixed=slugs[0]; rounds=[]
    for _ in range(n-1):
        order=[fixed]+rot; rounds.append([(order[i],order[n-1-i]) for i in range(n//2)])
        rot=[rot[-1]]+rot[:-1]
    return [list(rd) for rd in rounds]+[[(a,h) for h,a in rd] for rd in rounds]

def mw_date(season_start_year, mw):
    # 22 MW, ~ every 7 days from mid-Aug; current-season MW18 ~ 2026-02-14
    base=date(season_start_year,8,16)
    d=base+timedelta(days=(mw-1)*11)  # ~11d spacing -> season Aug..Apr
    return d

# accumulate career stats
career={s:dict(apps=0,goals=0,assists=0,minutes=0,yellows=0,reds=0,seasons=[]) for s in allplayers}

def simulate_season(year, played_through, seed_off):
    r=random.Random(SEED+seed_off)
    strengths={s:STR[s]+r.uniform(-0.05,0.05) for s in STR}
    slugs=[c['slug'] for c in clubs]
    sched=schedule(slugs)
    standings={s:dict(p=0,w=0,d=0,l=0,gf=0,ga=0,pts=0) for s in slugs}
    season_pstats={s:dict(apps=0,goals=0,assists=0,minutes=0,yellows=0,reds=0) for s in allplayers}
    matches=[]
    for mw,pairs in enumerate(sched, start=1):
        d=mw_date(year,mw)
        for (h,a) in pairs:
            m=dict(mw=mw, date=d.isoformat(), day=d.strftime('%a'), home=h, away=a,
                   kickoff=r.choice(["13:00","14:00","15:00","16:30","17:00","19:45"]),
                   venue=next(c['stadium'] for c in clubs if c['slug']==h),
                   attendance=r.randint(int(0.55*38400), 38400) if h=='harborside' else r.randint(9000,28000),
                   referee=r.choice(["L. Caruso","M. Fenton","P. Alvarez","D. Whitlock","S. Okafor"]),
                   weather=r.choice(["9°C, light rain","14°C, clear","6°C, overcast","18°C, sunny","4°C, windy"]),
                   played=mw<=played_through)
            if m['played']:
                hg=pois(r, max(0.3,1.5+(strengths[h]-strengths[a])*0.9+0.35))
                ag=pois(r, max(0.2,1.2+(strengths[a]-strengths[h])*0.9))
                m['hg'],m['ag']=hg,ag
                # goal/card events
                goals=[]; cards=[]
                for team,gc in ((h,hg),(a,ag)):
                    for _ in range(gc):
                        sc=weighted_pick(r, squads[team], SCORE_W)
                        note=r.choice(GOAL_NOTES)
                        goals.append(dict(minute=r.randint(1,90), team=team, scorer=sc['name'], scorerSlug=sc['slug'], note=note))
                        season_pstats[sc['slug']]['goals']+=1
                        if r.random()<0.62:
                            asst=weighted_pick(r, [p for p in squads[team] if p['slug']!=sc['slug']], ASSIST_W)
                            goals[-1]['assist']=asst['name']; season_pstats[asst['slug']]['assists']+=1
                for team in (h,a):
                    for _ in range(pois(r,1.4)):
                        pl=r.choice(squads[team]); cards.append(dict(minute=r.randint(1,90),team=team,player=pl['name'],card='yellow'))
                        season_pstats[pl['slug']]['yellows']+=1
                    if r.random()<0.08:
                        pl=r.choice(squads[team]); cards.append(dict(minute=r.randint(60,90),team=team,player=pl['name'],card='red'))
                        season_pstats[pl['slug']]['reds']+=1
                goals.sort(key=lambda g:g['minute']); m['goals']=goals; m['cards']=sorted(cards,key=lambda c:c['minute'])
                # appearances: 11 starters + up to 3 subs per side (weight lower nums as starters)
                for team in (h,a):
                    sq=sorted(squads[team], key=lambda p:p['num'])
                    starters=sq[:11]; subs=r.sample(sq[11:], k=min(3,len(sq)-11))
                    for p in starters: season_pstats[p['slug']]['apps']+=1; season_pstats[p['slug']]['minutes']+=r.randint(70,90)
                    for p in subs: season_pstats[p['slug']]['apps']+=1; season_pstats[p['slug']]['minutes']+=r.randint(5,30)
                # match stats bars
                pos_h=r.randint(38,66); m['stats']=[
                    dict(label='Possession',h="%d%%"%pos_h,a="%d%%"%(100-pos_h),hp=pos_h,ap=100-pos_h),
                    dict(label='Shots',h=str(hg*2+r.randint(3,9)),a=str(ag*2+r.randint(2,7))),
                    dict(label='Shots on target',h=str(hg+r.randint(1,5)),a=str(ag+r.randint(1,4))),
                    dict(label='Corners',h=str(r.randint(2,10)),a=str(r.randint(1,8))),
                    dict(label='Fouls',h=str(r.randint(6,15)),a=str(r.randint(6,15))),
                    dict(label='Pass accuracy',h="%d%%"%r.randint(72,89),a="%d%%"%r.randint(68,86)),
                    dict(label='xG',h="%.1f"%(hg*0.8+r.uniform(0.1,0.9)),a="%.1f"%(ag*0.8+r.uniform(0.1,0.7))),
                ]
                for club,gf,ga in ((h,hg,ag),(a,ag,hg)):
                    st=standings[club]; st['p']+=1; st['gf']+=gf; st['ga']+=ga
                    st['w' if gf>ga else 'd' if gf==ga else 'l']+=1; st['pts']+=3 if gf>ga else 1 if gf==ga else 0
            matches.append(m)
    # standings table
    tbl=[]
    for s in slugs:
        st=standings[s]; gd=st['gf']-st['ga']
        tbl.append(dict(club=s,p=st['p'],w=st['w'],d=st['d'],l=st['l'],gf=st['gf'],ga=st['ga'],
                        gd=('+%d'%gd if gd>=0 else str(gd)),pts=st['pts']))
    tbl.sort(key=lambda x:(-x['pts'],-int(x['gd'].replace('+','')),-x['gf']))
    for i,row in enumerate(tbl,1): row['pos']=i
    # roll into career
    for slug,ps in season_pstats.items():
        for k in ('apps','goals','assists','minutes','yellows','reds'): career[slug][k]+=ps[k]
    return matches, tbl, season_pstats

SEASONS=[(2023,'2023-24',22),(2024,'2024-25',22),(2025,'2025-26',17)]
season_out={}
for (yr,label,pt) in SEASONS:
    matches,tbl,ps=simulate_season(yr,pt,yr)
    season_out[label]=dict(matches=matches,standings=tbl,playerStats=ps)

# player profiles (with current-season stats + career + last5)
cur=season_out['2025-26']
def player_last5(slug, club):
    played=[m for m in cur['matches'] if m['played'] and (m['home']==club or m['away']==club)]
    played=sorted(played,key=lambda m:m['mw'])[-5:]
    out=[]
    for m in played:
        opp=m['away'] if m['home']==club else m['home']; ven='H' if m['home']==club else 'A'
        g=sum(1 for x in m['goals'] if x['scorerSlug']==slug); a=sum(1 for x in m['goals'] if x.get('assist')==allplayers[slug]['name'])
        out.append(dict(opp=opp,venue=ven,g=g,a=a,rating=round(random.Random(hash(slug+m['date'])%99999).uniform(6.2,9.3),1)))
    return out
players_full=[]
for slug,p in allplayers.items():
    cs=cur['playerStats'][slug]; cr=career[slug]
    players_full.append(dict(**p,
        season=dict(apps=cs['apps'],goals=cs['goals'],assists=cs['assists'],minutes=cs['minutes'],yellows=cs['yellows'],reds=cs['reds']),
        career=dict(apps=cr['apps'],goals=cr['goals'],assists=cr['assists']),
        last5=player_last5(slug,p['club'])))

# stories: 1 match report per matchweek (biggest-margin played game) for current season
stories=[]
by_mw={}
for m in cur['matches']:
    if m['played']: by_mw.setdefault(m['mw'],[]).append(m)
for mw in sorted(by_mw)[-6:][::-1]:
    m=max(by_mw[mw], key=lambda x:abs(x['hg']-x['ag']))
    win,los,ws,ls=(m['home'],m['away'],m['hg'],m['ag']) if m['hg']>=m['ag'] else (m['away'],m['home'],m['ag'],m['hg'])
    top=max(m['goals'],key=lambda g:1,default=None) if m['goals'] else None
    hl=(top['scorer'] if top else "The visitors")
    stories.append(dict(slug="mw%d-%s-%s"%(mw,win,los), headline="%s see off %s in Matchweek %d"%(clubname[win],clubname[los],mw),
        dek="%s ran out %d-%d winners at %s, with %s among the scorers."%(clubname[win],ws,ls,m['venue'],hl),
        author=random.Random(mw).choice(["Sasha Quinn","Matt Crain","J. Park","D. Lo"]), date=m['date'],
        image="story-%d.jpg"%((mw%3)+1), featured=(mw==max(by_mw)), category="Match Report",
        body=["%s claimed a %d-%d win over %s."%(clubname[win],ws,ls,clubname[los]),
              "The result moves them in the table as the season enters its business end."]))

W('clubs.json',clubs); W('squads.json',squads); W('players.json',players_full)
W('standings.json',cur['standings'])
played=[m for m in cur['matches'] if m['played']]
W('recentResults.json',list(reversed(sorted(played,key=lambda m:(m['mw'],m['date']))))[:9])
_unplayed=[m for m in cur['matches'] if not m['played']]
_next_weeks=sorted(set(m['mw'] for m in _unplayed))[:5]  # next 5 matchweeks
W('upcomingFixtures.json',[m for m in _unplayed if m['mw'] in _next_weeks])
W('matches.json',{k:v['matches'] for k,v in season_out.items()})
W('seasons.json',{k:dict(standings=v['standings']) for k,v in season_out.items()})
W('stories.json',stories)
# featured match = latest played
hb_wins=[m for m in played if m['home']=='harborside' and m['hg']>m['ag']]
feat=max(hb_wins,key=lambda m:(m['hg']-m['ag'],m['mw'])) if hb_wins else max(played,key=lambda m:(m['mw'],m['date']))
W('featuredMatch.json',feat)

# top scorers (nice sanity view)
scorers=sorted(players_full,key=lambda p:-p['season']['goals'])[:5]
print("clubs %d | players %d | seasons %s"%(len(clubs),len(players_full),list(season_out.keys())))
print("current standings top5:", [(r['pos'],clubshort[r['club']],r['pts']) for r in cur['standings'][:5]])
print("top scorers 2025-26:", [(p['name'],clubshort[p['club']],p['season']['goals']) for p in scorers])
print("featured match:", feat['home'],feat['hg'],'-',feat['ag'],feat['away'],"| goals:",len(feat['goals']))
print("stories:", len(stories), "| e.g.:", stories[0]['headline'])
