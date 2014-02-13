import os
import requests
import bs4


homework_dir = "homework"

def parse_language(lang):
	res = "unknown"
	lang = lang.lower()
	if "c++" in lang or "g++" in lang:
		res = "cpp"
	elif "java" or "JAVA" in lang:
		res = "java"
	elif "c" or "C" in lang:
		res = "c"
	return res

def get_samples(judge_id, prob_id, contest_id, students_files = []):

    session = requests.session()

#get list of sample solutions
    sample_data = {"sEcho":"2", "iColumns":"15", "sColumns":"", "iDisplayStart":"0", "iDisplayLength":"9999999", "mDataProp_0":"0", "mDataProp_1":"1", "mDataProp_2":"2", "mDataProp_3":"3", "mDataProp_4":"4", "mDataProp_5":"5", "mDataProp_6":"6", "mDataProp_7":"7", "mDataProp_8":"8", "mDataProp_9":"9", "mDataProp_10":"10", "mDataProp_11":"11", "mDataProp_12":"12", "mDataProp_13":"13", "mDataProp_14":"14", "un":"", "OJId":judge_id, "probNum":str(prob_id), "res":"1"} #res 1 = accepted
    sample_headers = { "User-Agent": "Mozilla/5.0","Referer:http":"//acm.hust.edu.cn/vjudge/problem/status.action"}
    sample_res = session.post("http://acm.hust.edu.cn/vjudge/problem/fetchStatus.action", data=sample_data, headers=sample_headers)
    sample_solutions =  sample_res.json()["aaData"]

#save sample solutions that we can access
    sample_solution_dir = os.path.join(homework_dir, str(contest_id), judge_id+str(prob_id), "sample_solutions")
    if not os.path.exists(sample_solution_dir):
        os.makedirs(sample_solution_dir)

    for sol in sample_solutions:
        sample_solution_res = session.get("http://acm.hust.edu.cn/vjudge/problem/viewSource.action?id="+str(sol[0]))
        sample_solution_soup = bs4.BeautifulSoup(sample_solution_res.text)
        sample_solution_filename = sol[1]+"."+parse_language(sol[6])

        if not sample_solution_soup.pre is None and not sample_solution_filename in students_files:
            print "Downloading", sample_solution_filename, "...",
            sample_solution_path = os.path.join(sample_solution_dir, sample_solution_filename)
            sample_solution_file = open(sample_solution_path, "w")
            sample_solution_file.write(sample_solution_soup.pre.contents[0].encode("utf-8"))
            sample_solution_file.close()
            print "Done."

    session.close()

get_samples("UVA",100,1234,["sususu.java"])
