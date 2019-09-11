#!/usr/bin/env python

from downloader import *
import youtube_dl
import os
import sys
import argparse

parser = argparse.ArgumentParser(add_help=False, description=('Download Youtube comments without using the Youtube API'))
parser.add_argument('--folder', help='Base folder', required=True)
parser.add_argument('--languages', nargs='+', required=True)
parser.add_argument('--limit', default=50, type=int)
args = parser.parse_args()

languages = args.languages
baseFolder = args.folder
limit = args.limit

def doLang(lang, baseFolder, limit):
    inputFolder = baseFolder + "/" + lang
    outputFolder = baseFolder + "/" + lang + "_comments"
    ydl = youtube_dl.YoutubeDL({'outtmpl': '%(id)s%(ext)s'})

    for root, dirs, files in os.walk(inputFolder):
        for filename in files:
            if not filename.endswith(".txt"):
                continue
            filepath = inputFolder + "/" + filename
            with open(filepath) as fp:
                for line in fp.readlines():
                    line = line.strip()
                    if len(line) == 0:
                        continue

                    youtube_id = line

                    outfprefix = outputFolder + "/" + filename.replace(".txt", "") + "." + line
                    output = outfprefix + ".comments.json"
                    exists = os.path.isfile(output)
                    if exists:
                        continue
                        # print("Crawling for language: " + lang + " finished.")
                        # return

                    ytlink = "http://www.youtube.com/watch?v=" + line
                    print("Video: " + ytlink)
                    print("Download metadata")

                    try:
                        metainfo = ydl.extract_info(ytlink, download=False)
                        meta_file = open(outfprefix + ".meta.json", "w")
                        meta_file.write(json.dumps(metainfo, ensure_ascii=False))
                        meta_file.close()
                    except Exception as e:
                        meta_file = open(outfprefix + ".meta.json", "w")
                        meta_file.close()

                    print('Downloading Youtube comments for video:', youtube_id)
                    count = 0
                    try:
                        with io.open(output, 'w', encoding='utf8') as fp:
                            comments = []
                            for comment in download_comments(youtube_id):
                                comments.append(comment)
                                count += 1
                                sys.stdout.write('Downloaded %d comment(s)\r' % count)
                                sys.stdout.flush()
                                if limit and count >= limit:
                                    break
                            print(json.dumps(comments, ensure_ascii=False), file=fp)
                    except Exception as e:
                        print('Error:', str(e))
                    print('\nDone!')


for lang in languages:
    doLang(lang, baseFolder, limit)
