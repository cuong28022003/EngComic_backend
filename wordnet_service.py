import os
import sys
import nltk
from flask import Flask, request, jsonify
from flask_cors import CORS

# Ensure wordnet data is available
try:
    from nltk.corpus import wordnet as wn
    wn.synsets('test')
except Exception:
    print("Downloading NLTK WordNet corpus...")
    nltk.download('wordnet')
    from nltk.corpus import wordnet as wn

app = Flask(__name__)
CORS(app)

def extract_word_family(word):
    clean_word = word.strip().lower()
    family = {
        'rootWord': clean_word,
        'noun': None,
        'verb': None,
        'adjective': None,
        'adverb': None,
        'members': []
    }

    synsets = wn.synsets(clean_word)
    if not synsets:
        return family

    # Determine input word's own part of speech
    primary_pos = synsets[0].pos()
    if primary_pos == 'n':
        family['noun'] = clean_word
    elif primary_pos == 'v':
        family['verb'] = clean_word
    elif primary_pos in ['a', 's']:
        family['adjective'] = clean_word
    elif primary_pos == 'r':
        family['adverb'] = clean_word

    nouns = set()
    verbs = set()
    adjectives = set()
    adverbs = set()

    for synset in synsets:
        for lemma in synset.lemmas():
            # Check lemmas matching current word
            if lemma.name().lower().replace('_', ' ') == clean_word or lemma.name().lower() == clean_word:
                for rel in lemma.derivationally_related_forms():
                    rel_name = rel.name().replace('_', ' ').lower()
                    pos = rel.synset().pos()

                    if pos == 'n':
                        nouns.add(rel_name)
                    elif pos == 'v':
                        verbs.add(rel_name)
                    elif pos in ['a', 's']:
                        adjectives.add(rel_name)
                    elif pos == 'r':
                        adverbs.add(rel_name)

    if nouns and not family['noun']:
        family['noun'] = sorted(list(nouns), key=len)[0]
    if verbs and not family['verb']:
        family['verb'] = sorted(list(verbs), key=len)[0]
    if adjectives and not family['adjective']:
        family['adjective'] = sorted(list(adjectives), key=len)[0]
    if adverbs and not family['adverb']:
        family['adverb'] = sorted(list(adverbs), key=len)[0]

    members = []
    if family['noun']:
        members.append({'word': family['noun'], 'partOfSpeech': 'noun'})
    if family['verb']:
        members.append({'word': family['verb'], 'partOfSpeech': 'verb'})
    if family['adjective']:
        members.append({'word': family['adjective'], 'partOfSpeech': 'adjective'})
    if family['adverb']:
        members.append({'word': family['adverb'], 'partOfSpeech': 'adverb'})

    family['members'] = members
    return family

@app.route('/api/wordnet', methods=['GET'])
def wordnet_endpoint():
    word = request.args.get('word', '').strip()
    if not word:
        return jsonify({'error': 'Word parameter is required'}), 400

    result = extract_word_family(word)
    return jsonify(result)

if __name__ == '__main__':
    port = 5000
    print(f"WordNet Microservice started on http://localhost:{port}/api/wordnet")
    app.run(host='0.0.0.0', port=port, debug=False)
