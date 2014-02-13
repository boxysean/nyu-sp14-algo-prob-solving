import os
import requests
import bs4
import re
import glob
import subprocess


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

def get_samples(judge_id, prob_id, contest_id, students_files = [], max_downloads = -1):

    session = requests.session()

#get list of sample solutions
    sample_data = {"sEcho":"2", "iColumns":"15", "sColumns":"", "iDisplayStart":"0", "iDisplayLength":"9999999", "mDataProp_0":"0", "mDataProp_1":"1", "mDataProp_2":"2", "mDataProp_3":"3", "mDataProp_4":"4", "mDataProp_5":"5", "mDataProp_6":"6", "mDataProp_7":"7", "mDataProp_8":"8", "mDataProp_9":"9", "mDataProp_10":"10", "mDataProp_11":"11", "mDataProp_12":"12", "mDataProp_13":"13", "mDataProp_14":"14", "un":"", "OJId":judge_id, "probNum":str(prob_id), "res":"1"} #res 1 = accepted
    sample_headers = { "User-Agent": "Mozilla/5.0","Referer:http":"//acm.hust.edu.cn/vjudge/problem/status.action"}
    sample_res = session.post("http://acm.hust.edu.cn/vjudge/problem/fetchStatus.action", data=sample_data, headers=sample_headers)
    sample_solutions =  sample_res.json()["aaData"]

    sample_fnames = []

#save sample solutions that we can access
    sample_solution_dir = os.path.join(homework_dir, str(contest_id), judge_id+str(prob_id), "sample_solutions")
    if not os.path.exists(sample_solution_dir):
        os.makedirs(sample_solution_dir)

    for sol in sample_solutions:
        sample_solution_res = session.get("http://acm.hust.edu.cn/vjudge/problem/viewSource.action?id="+str(sol[0]))
        sample_solution_soup = bs4.BeautifulSoup(sample_solution_res.text)
        sample_solution_filename = sol[1]+"."+parse_language(sol[6])

        if not sample_solution_soup.pre is None and not sample_solution_filename in students_files and not max_downloads == 0:
            max_downloads -= 1
            sample_fnames += sample_solution_filename
            print "Downloading", sample_solution_filename, "...",
            sample_solution_path = os.path.join(sample_solution_dir, sample_solution_filename)
            sample_solution_file = open(sample_solution_path, "w")
            sample_solution_file.write(sample_solution_soup.pre.contents[0].encode("utf-8"))
            sample_solution_file.close()
            print "Done."

    session.close()

    return sample_fnames

def moss_filter(percentage_threshhold, directory, language):

#upload all files in directory with given language to moss, delete any with match higher than threshhold
    try:
        files = glob.glob(directory+"/*."+language)
        moss_output = subprocess.check_output(["./moss", "-l", "c","-m",str(len(files))]+files).split("\n")
        moss_output.reverse()
    except Exception, e:
        print "Moss Filter Failed for "+language+"!"
        return

    moss_res = requests.get(moss_output[1])
    moss_soup = bs4.BeautifulSoup(moss_res.text)
    moss_matches = moss_soup.findAll("td")
    for match in moss_matches:
        if "<td align=" in str(match):
            moss_matches.remove(match)
    for index in range(0,len(moss_matches)/2):
        try:
            file1_path = re.search(str(directory)+".*\."+language,str(moss_matches[2*index-2])).group(0)
            file2_path = re.search(str(directory)+".*\."+language,str(moss_matches[2*index-1])).group(0)
            file1_percentage = re.search("[0-9]+",re.search("\(.*\)",str(moss_matches[2*index-2])).group(0)).group(0)
            file2_percentage = re.search("[0-9]+",re.search("\(.*\)",str(moss_matches[2*index-1])).group(0)).group(0)
            if int(file1_percentage) > percentage_threshhold or int(file2_percentage) > percentage_threshhold:
                if int(file1_percentage) > int(file2_percentage):
                    if os.path.exists(file1_path):
                        os.remove(str(file1_path))
                        print "deleting", file1_path, file1_percentage
                else:
                    if os.path.exists(file2_path):
                       os.remove(file2_path)
                       print "deleting", file2_path, file2_percentage

        except:
            print "Something didn't match in the " + language + " moss output!"
            print index*2-2, moss_matches[2*index-2]
            print index*2-1, moss_matches[2*index-1]

def download_submissions(contest_id, username, password):

    contest_status = requests.get("http://acm.hust.edu.cn/vjudge/contest/view.action?cid=" + str(contest_id) + "#status")
    num_problems = len(str(bs4.BeautifulSoup(contest_status.text).findAll("select")[1]).split("\n"))-2
    file_names = []

    session = requests.session()
    session.post("http://acm.hust.edu.cn/vjudge/user/login.action", data = {"username" : username , "password" : password} )

    for problem_number in range(1,2):

        data = {"cid":str(contest_id),"sEcho":"2", "iColumns":"13", "sColumns":"", "iDisplayStart":"0", "iDisplayLength":"9999999", "mDataProp_0":"0", "mDataProp_1":"1", "mDataProp_2":"2", "mDataProp_3":"3", "mDataProp_4":"4", "mDataProp_5":"5", "mDataProp_6":"6", "mDataProp_7":"7", "mDataProp_8":"8", "mDataProp_9":"9", "mDataProp_10":"10", "mDataProp_11":"11", "mDataProp_12":"12", "un":"", "num":str(chr(65+problem_number)), "res":"1", "sEcho":"2"}
        #data = { "sEcho": 1, "iColumns": "13", "sColumns": "", "iDisplayStart": "0", "iDisplayLength": "999999", "mDataProp_0": "0", "mDataProp_1": "1", "mDataProp_2": "2", "mDataProp_3": "3", "mDataProp_4": "4", "mDataProp_5": "5", "mDataProp_6": "6", "mDataProp_7": "7", "mDataProp_8": "8", "mDataProp_9": "9", "mDataProp_10": "10", "mDataProp_11": "11", "mDataProp_12": "12", "un": "", "num": "-", "res": "0" }
        headers = { "User-Agent": "Mozilla/5.0","Referer:http":"http://acm.hust.edu.cn/vjudge/contest/view.action?cid=" + str(contest_id)}
        res = session.post("http://acm.hust.edu.cn/vjudge/problem/fetchStatus.action?cid="+str(contest_id), data=data, headers=headers)
        #res = requests.get("http://acm.hust.edu.cn/vjudge/contest/view.action?cid=" + str(contest_id) + "#status//"+str(chr(65+problem_number))+"/1", data=data, headers=headers)
        print res.text
        solutions =  res.json()["aaData"]
        print solutions


#save sample solutions that we can access
        solution_dir = os.path.join(homework_dir, str(contest_id), judge_id+str(prob_id), "student_solutions")
        if not os.path.exists(solution_dir):
            os.makedirs(solution_dir)

        for sol in solutions:
            solution_res = session.get("http://acm.hust.edu.cn/vjudge/problem/viewSource.action?id="+str(sol[0]))
            solution_soup = bs4.BeautifulSoup(solution_res.text)
            solution_filename = sol[1]+"."+parse_language(sol[6])

            if not solution_soup.pre is None and not solution_filename in students_files and not max_downloads == 0:
                max_downloads -= 1
                fnames += solution_filename
                print "Downloading", solution_filename, "...",
                solution_path = os.path.join(solution_dir, solution_filename)
                solution_file = open(solution_path, "w")
                solution_file.write(solution_soup.pre.contents[0].encode("utf-8"))
                solution_file.close()
                print "Done."

        session.close()

        return fnames

#get_samples("UVA",100,1234,["sususu.java"],max_downloads = 100)
#moss_filter(50,"homework/1234/UVA100/sample_solutions/","cpp")
download_submissions(38690, "CS480Admin", "1491625")
