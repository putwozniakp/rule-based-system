""" Input Format
<rule>:<question>
<last_rule>:<last_ans>
<last_rule>:<last_ans>
<ans1>+<final>
<ans2>
<ans3>
--- <delimiter>
"""

from typing import List

RULE_FORMAT="""
rule "{0}"
    when 
        {2}
        not Fact(target == "{0}")
    then
        RuleSystem.INSTANCE.setQuestion("{1}");
        {3}
end
"""
ADD_OPTION_STRING="""
        RuleSystem.INSTANCE.addOption("{1}", (KieSession ksess) -> {{
        	ksess.insert(new Fact("{0}", "{1}"));
        }});
"""
RESULT_STRING="""
rule "RESULT: {0}"
	when
		Fact(target == "{1}", state == "{2}")
	then
		RuleSystem.INSTANCE.setQuestion("RESULT: {0}");
end
"""

def print_rule(rule: str, question: str, prev: List[tuple], answers: List[tuple]):
    """ Print given drools rule

    Parameters:
    rule (str): Rule name - how drools should encode the state 
    question (str): Prompt seen to the user
    prev (List[(str, str)]): List of pairs: (rule, answer) which point to this rule
    answers (List[str, str]): List of pairs (answer, terminal | None),
        possible answer to this question + RESULT if terminal question
    """
    
    fact_format = 'Fact(target == "{0}", state == "{1}")'
    when_part = '\n\t\tor '.join(fact_format.format(target, state) for target, state in prev)
    option_part =  '\n'.join(ADD_OPTION_STRING.format(rule, answer) for answer, _ in answers)

    rule_str = RULE_FORMAT.format(rule, question, when_part, option_part)
    print(rule_str)

    for answer, terminal_result in answers:
        if terminal_result is None:
            continue

        print(RESULT_STRING.format(terminal_result, rule, answer))
        
if __name__ == "__main__":
    RULE_FILENAME = 'video_game_rules.txt'

    with open(RULE_FILENAME, 'r') as input_file:
        whole_input = input_file.read().strip()

        lines = [ line.strip() for line in whole_input.split('\n') ]

        n = len(lines)
        idx = 0

        while idx < n:
            if lines[idx].startswith('-'):
                idx += 1
                continue;

            rule, question = [ token.strip() for token in lines[idx].split(':') ]
            idx += 1
            
            prev = []
            while idx < n and lines[idx].find(':') != -1:
                prev_rule, prev_ans = [ token.strip() for token in lines[idx].split(':') ]
                prev.append((prev_rule, prev_ans))
                idx += 1
                
            if idx >= n:
                break

            answers = []
            while idx < n and not lines[idx].startswith('-'):
                if lines[idx].find('+') == -1:
                    answers.append((lines[idx].strip(), None))
                else:
                    answ, term = [ token.strip() for token in lines[idx].split('+') ]
                    answers.append((answ, term))
                idx += 1

            print_rule(rule, question, prev, answers)
            idx += 1



