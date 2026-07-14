#!/usr/bin/env python3
"""ACPL league-data generator -> JSON for the 12 datasources.
Deterministic (fixed seed). Simulates 3 seasons: fixtures, results, standings,
player goal/assist/apps/cards attribution (season + career), match summaries, stories."""
import json, os, random, zlib
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

def shash(text):
    """Stable string hash (crc32) since the built-in is randomized per process."""
    return zlib.crc32(text.encode('utf-8'))

def harborside_squad():
    out=[]
    for num,pos,nm in HARBORSIDE_SQUAD:
        r=random.Random(SEED+shash(nm)%9999)
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
    # Circle-method pairings with greedy venue assignment: within each round, the club coming off an
    # away game gets home (ties broken by fewest homes so far). Deterministic, no long H/A runs.
    # Second half of the season mirrors the first with venues reversed.
    n=len(slugs); rot=slugs[1:]; fixed=slugs[0]; base=[]
    for _ in range(n-1):
        order=[fixed]+rot
        base.append([(order[i],order[n-1-i]) for i in range(n//2)])
        rot=[rot[-1]]+rot[:-1]
    last={c:None for c in slugs}; homes={c:0 for c in slugs}; rounds=[]
    for rd in base:
        pairs=[]
        for x,y in rd:
            def pref(t):
                return (1 if last[t]=='A' else -1 if last[t]=='H' else 0)-homes[t]*0.01
            if pref(y)>pref(x): x,y=y,x
            pairs.append((x,y))
        for h,a in pairs:
            last[h]='H'; last[a]='A'; homes[h]+=1
        rounds.append(pairs)
    return rounds+[[(a,h) for h,a in rd] for rd in rounds]

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
                # appearances: 11 starters + up to 3 subs per side. Lineups are chosen
                # DETERMINISTICALLY (hash-based rotation, event participants forced in) so squad
                # apps stay within real matchday limits and exactly one GK plays per side.
                # RNG parity: the r.sample / r.randint draw SEQUENCE is kept identical to the old
                # code (same counts over same-size populations), so match results are unchanged.
                names={p['name']:p['slug'] for t in (h,a) for p in squads[t]}
                event_slugs={g['scorerSlug'] for g in goals}
                event_slugs.update(names[g['assist']] for g in goals if g.get('assist') in names)
                event_slugs.update(names[c['player']] for c in cards if c['player'] in names)
                for team in (h,a):
                    sq=sorted(squads[team], key=lambda p:p['num'])
                    k=min(3,len(sq)-11)
                    _legacy_subs=r.sample(sq[11:], k=k)  # draw kept for RNG-stream parity only
                    ev=[p for p in sq if p['slug'] in event_slugs]
                    gks=[p for p in sq if p['pos']=='GK']
                    ev_gk=[p for p in ev if p['pos']=='GK']
                    # one GK: an event GK if any, else rotate the backup in every 6th matchweek
                    gk=ev_gk[0] if ev_gk else (gks[1] if (mw%6==0 and len(gks)>1) else gks[0])
                    outfield=[p for p in sq if p['pos']!='GK']
                    ev_out=[p for p in outfield if p['slug'] in event_slugs]
                    rest=[p for p in outfield if p['slug'] not in event_slugs]
                    rot=shash(team+m['date'])%3  # rest 0-2 regulars each match
                    pool=rest[rot:]+rest[:rot]
                    starters=([gk]+ev_out+pool)[:11]
                    bench=[p for p in sq if p not in starters]
                    # subs come from the outfield bench — backup keepers don't come on every week
                    bench_out=[p for p in bench if p['pos']!='GK']
                    subs=(bench_out+[p for p in bench if p['pos']=='GK'])[:k]
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

# Stable sequential match ids (1001+), in season then array order. Match detail pages route by id
# (/matches/{id}.html) — home-vs-away isn't unique across seasons. recentResults/upcomingFixtures
# share these dict objects, so the id propagates to those views too.
_mid=1001
for (yr,label,pt) in SEASONS:
    for m in season_out[label]['matches']:
        m['id']=_mid; _mid+=1

# player profiles (with current-season stats + career + last5)
cur=season_out['2025-26']
def player_last5(slug, club):
    played=[m for m in cur['matches'] if m['played'] and (m['home']==club or m['away']==club)]
    played=sorted(played,key=lambda m:m['mw'])[-5:]
    out=[]
    for m in played:
        opp=m['away'] if m['home']==club else m['home']; ven='H' if m['home']==club else 'A'
        g=sum(1 for x in m['goals'] if x['scorerSlug']==slug); a=sum(1 for x in m['goals'] if x.get('assist')==allplayers[slug]['name'])
        us=m['hg'] if m['home']==club else m['ag']; them=m['ag'] if m['home']==club else m['hg']
        res=('W' if us>them else 'D' if us==them else 'L')+' %d–%d'%(m['hg'],m['ag'])
        base=random.Random(shash(slug+m['date'])%99999).uniform(5.9,7.4)
        rating=min(9.8, base + 0.7*g + 0.4*a + (0.4 if us>them else -0.3 if us<them else 0.0))
        out.append(dict(opp=opp,venue=ven,mw=m['mw'],id=m['id'],res=res,g=g,a=a,rating=round(rating,1)))
    return out
players_full=[]
for slug,p in allplayers.items():
    cs=cur['playerStats'][slug]; cr=career[slug]
    players_full.append(dict(**p,
        season=dict(apps=cs['apps'],goals=cs['goals'],assists=cs['assists'],minutes=cs['minutes'],yellows=cs['yellows'],reds=cs['reds']),
        career=dict(apps=cr['apps'],goals=cr['goals'],assists=cr['assists']),
        last5=player_last5(slug,p['club'])))

# stories: a mix of match reports, features, previews, an interview and analysis
stories=[]
_auth_pool=["Sasha Quinn","Matt Crain","Jordan Park","Dana Lo","Priya Nair","Tom Beckett","Rita Okafor"]
def _auth(seed): return random.Random(seed).choice(_auth_pool)
def _img(i): return "story-%d.jpg"%((i%3)+1)
_stand=cur['standings']  # sorted by position
_scorers=sorted(players_full,key=lambda p:p['season']['goals'],reverse=True)
_played=[m for m in cur['matches'] if m['played']]
_latest=max(m['date'] for m in _played)
_future=sorted([m for m in cur['matches'] if not m['played']],key=lambda x:(x['mw'],x['date']))


def _scorer_sentence(m):
    if not m['goals']: return "Chances were scarce throughout, and neither goalkeeper was seriously tested until late on."
    parts=["%s (%d')"%(g['scorer'],g['minute']) for g in m['goals']]
    return "The goals came from "+", ".join(parts[:-1])+(" and " if len(parts)>1 else "")+parts[-1]+"."

def _report_body(m,win,los,ws,ls):
    stats={x['label']:x for x in m.get('stats',[])}
    poss=stats.get('Possession',{}).get('h','')
    body=["%s claimed a %d-%d win over %s in front of %s at %s, a result that keeps the Matchweek %d talking points coming."%(
        clubname[win],ws,ls,clubname[los],format(m.get('attendance',0),',d'),m['venue'],m['mw'])]
    body.append(_scorer_sentence(m))
    if poss:
        body.append("The hosts saw %s of the ball, registering %s shots to the visitors' %s, and referee %s kept the game flowing (%s)."%(
            poss,stats.get('Shots',{}).get('h','-'),stats.get('Shots',{}).get('a','-'),m.get('referee','the officials'),m.get('weather','cool conditions')))
    body.append("%s will feel the scoreline was a fair reflection of the contest, while %s head home knowing the margins at this level remain unforgiving."%(
        clubname[win],clubname[los]))
    body.append("Attention now turns to the next round of fixtures, with both camps expected to rotate as the schedule tightens.")
    return body


def _ord(n):
    return "%d%s"%(n,{1:"st",2:"nd",3:"rd"}.get(n if n<20 else n%10,"th"))

# Match reports — biggest-margin game of each of the last 5 played matchweeks
_by_mw={}
for m in _played: _by_mw.setdefault(m['mw'],[]).append(m)
for i,mw in enumerate(sorted(_by_mw)[-5:][::-1]):
    m=max(_by_mw[mw],key=lambda x:abs(x['hg']-x['ag']))
    win,los,ws,ls=(m['home'],m['away'],m['hg'],m['ag']) if m['hg']>=m['ag'] else (m['away'],m['home'],m['ag'],m['hg'])
    hl=m['goals'][0]['scorer'] if m['goals'] else "the visitors"
    margin=ws-ls
    verb="edge" if margin<=1 else "see off" if margin==2 else "cruise past" if margin==3 else "dismantle"
    dek=[
        "%s ran out %d-%d winners at %s, with %s among the scorers."%(clubname[win],ws,ls,m['venue'],hl),
        "A %d-%d scoreline at %s flattered nobody — %s got exactly what their performance deserved."%(ws,ls,m['venue'],clubname[win]),
        "%s made it a %s to forget for %s, winning %d-%d in front of %s."%(clubname[win],date.fromisoformat(m['date']).strftime('%A'),clubname[los],ws,ls,format(m['attendance'],',d')),
        "%s struck first and never looked back as %s fell %d-%d at %s."%(hl,clubname[los],ls,ws,m['venue']),
        "Another statement from %s: %d-%d over %s, and the gap keeps growing."%(clubname[win],ws,ls,clubname[los]),
    ][i%5]
    stories.append(dict(slug="mw%d-%s-%s"%(mw,win,los),category="Match Report",featured=False,
        headline="%s %s %s in Matchweek %d"%(clubname[win],verb,clubname[los],mw),
        dek=dek,
        author=_auth(mw),date=m['date'],image=_img(i),matchId=m['id'],
        body=_report_body(m,win,los,ws,ls)))

# Feature (hero) — the title race
_l=_stand
_h2h_left=sum(1 for _f in cur['matches'] if not _f['played'] and {_l[1]['club'],_l[2]['club']}=={_f['home'],_f['away']})
stories.append(dict(slug="feature-title-race",category="Feature",featured=True,
    headline="%s stretch clear at the summit"%clubname[_l[0]['club']],
    dek="%s lead the way on %d points, but %s and %s are refusing to let the title race settle."%(
        clubname[_l[0]['club']],_l[0]['pts'],clubname[_l[1]['club']],clubname[_l[2]['club']]),
    author=_auth(101),date=_latest,image="story-1.jpg",
    body=["With the business end of the season in view, %s sit top of the Atlantic Coast Premier League on %d points from %d games — a record of %d wins, %d draws and just %d defeat%s."%(
              clubname[_l[0]['club']],_l[0]['pts'],_l[0]['p'],_l[0]['w'],_l[0]['d'],_l[0]['l'],'' if _l[0]['l']==1 else 's'),
          "Behind them, %s (%d pts) and %s (%d pts) are refusing to blink, while %s's goal difference of %s is the division's best by some distance."%(
              clubname[_l[1]['club']],_l[1]['pts'],clubname[_l[2]['club']],_l[2]['pts'],clubname[_l[0]['club']],_l[0]['gd']),
          "The chasing pair meet each other %s before the season is out, meaning the leaders may only need to hold serve at home to keep the destiny of the title in their own hands."%(
              {0:"no more times",1:"once more"}.get(_h2h_left,"twice more")),
          "History urges caution: leads have evaporated at this stage before, and the fixture computer has saved several of the leaders' hardest away days for the run-in.",
          "What is beyond argument is that the front three have separated themselves from the pack — fourth place is already %d points off the podium."%(
              _l[2]['pts']-_l[3]['pts'])]))

# Feature — golden boot
_ts=_scorers[0]
stories.append(dict(slug="feature-golden-boot",category="Feature",featured=False,
    headline="%s leads the Golden Boot race"%_ts['name'],
    dek="%s of %s tops the scoring charts with %d goals this season."%(_ts['name'],clubname[_ts['club']],_ts['season']['goals']),
    author=_auth(102),date=_latest,image="story-2.jpg",
    body=["%s has been the league's standout finisher, with %d goals in %d appearances leaving the %s man at the top of the charts."%(
              _ts['name'],_ts['season']['goals'],_ts['season']['apps'],clubname[_ts['club']]),
          "The chasing pack is close behind: %s (%d) and %s (%d) can each overhaul the leader with one hot streak."%(
              _scorers[1]['name'],_scorers[1]['season']['goals'],_scorers[2]['name'],_scorers[2]['season']['goals']),
          "What separates the front-runner is consistency — goals spread across the season rather than bunched in a single purple patch.",
          "With %d matchweeks remaining, the Golden Boot may yet come down to which contender's side creates more in the final stretch."%(22-17)]))

# Analysis — relegation battle
_b=_stand[-3:]
stories.append(dict(slug="analysis-relegation-battle",category="Analysis",featured=False,
    headline="The fight to beat the drop goes to the wire",
    dek="%s, %s and %s are locked in a scrap at the foot of the table."%(clubname[_b[0]['club']],clubname[_b[1]['club']],clubname[_b[2]['club']]),
    author=_auth(103),date=_latest,image="story-3.jpg",
    body=["Only %d points separate %s in tenth from %s at the foot of the table — the tightest bottom three the league has seen in years."%(
              _b[0]['pts']-_b[2]['pts'],clubname[_b[0]['club']],clubname[_b[2]['club']]),
          "%s's problem is at both ends: %d scored, %d conceded, and a goal difference of %s that could yet decide their fate on the final day."%(
              clubname[_b[2]['club']],_b[2]['gf'],_b[2]['ga'],_b[2]['gd']),
          "%s have shown flashes — %d wins says they can live at this level — but back-to-back results have proven elusive."%(
              clubname[_b[1]['club']],_b[1]['w']),
          "The remaining head-to-heads between the bottom sides now look like cup finals; six-pointers is an overused phrase, but here it fits.",
          "Two go down. Nobody in the bottom four can afford to look away."]))

# Previews — the next three fixtures
_pos={row['club']:row['pos'] for row in _stand}
for i,f in enumerate(_future[:3]):
    stories.append(dict(slug="preview-mw%d-%s-%s"%(f['mw'],f['home'],f['away']),category="Preview",featured=False,
        headline="Preview: %s vs %s"%(clubname[f['home']],clubname[f['away']]),
        dek=[
            "%s-placed %s host %s (%s) at %s — kick-off %s."%(
                _ord(_pos[f['home']]),clubname[f['home']],clubname[f['away']],_ord(_pos[f['away']]),f['venue'],f['kickoff']),
            "%s make the trip to %s, where %s have made home advantage count all season. Thursday, %s."%(
                clubname[f['away']],f['venue'],clubname[f['home']],f['kickoff']),
            "Points at both ends of the table ride on %s against %s — expect %s to bring an edge. KO %s."%(
                clubname[f['home']],clubname[f['away']],f['venue'],f['kickoff']),
        ][i%3],
        author=_auth(200+i),date=(date.fromisoformat(f['date'])-timedelta(days=3)).isoformat(),image=_img(i),matchId=f['id'],
        body=[[
                  "%s welcome %s to %s in one of the standout fixtures of Matchweek %d, with kick-off at %s."%(
                      clubname[f['home']],clubname[f['away']],f['venue'],f['mw'],f['kickoff']),
                  "All eyes turn to %s on %s, where %s and %s meet with Matchweek %d points on the line — kick-off %s."%(
                      f['venue'],'Thursday',clubname[f['home']],clubname[f['away']],f['mw'],f['kickoff']),
                  "Matchweek %d brings %s to %s, and hosts %s know exactly what a result would mean; the whistle goes at %s."%(
                      f['mw'],clubname[f['away']],f['venue'],clubname[f['home']],f['kickoff']),
              ][i%3],
              "The hosts will look to make home advantage count, while the visitors travel knowing points are precious at this stage of the season.",
              "Team news is expected on the morning of the game; both camps reported clean bills of health after the last round.",
              "Referee %s takes charge. Forecast: %s."%(f.get('referee','TBC'),f.get('weather','mild'))]))

# Interview
_mgr=next(c['mgr'] for c in clubs if c['slug']==_l[0]['club'])
stories.append(dict(slug="interview-%s-manager"%_l[0]['club'],category="Interview",featured=False,
    headline="“We take it one game at a time”: %s on the title run-in"%_mgr,
    dek="The %s manager reflects on a season that has the club dreaming of the trophy."%clubname[_l[0]['club']],
    author=_auth(300),date=_latest,image="story-1.jpg",
    body=["%s sat down with us to talk through the season so far and what comes next for %s."%(_mgr,clubname[_l[0]['club']]),
          "\u201cWe take it one game at a time — I know everybody says that, but the moment you look two fixtures ahead is the moment you drop points,\u201d the manager said.",
          "On the title race: \u201cThe table looks kind today, but %d games is a long time in this league. Our jobs are won on cold nights against sides fighting for their lives.\u201d"%(22-17),
          "Asked about the squad's depth, there was a smile: \u201cCompetition for places is the best coach I have.\u201d",
          "The message to supporters was simple: keep coming, keep singing, and judge us in May."]))

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
